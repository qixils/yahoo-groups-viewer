import base64
import html
import json
import os
import re
import sys
import typing

from warcio.archiveiterator import ArchiveIterator

MESSAGE_ID = re.compile(r"^org\.archive\.yahoogroups:v1/group/[a-z_]+/message/(\d+)/raw$")
LINEBREAKS = re.compile(r"\r?\n")
SECTION_PREFIX = re.compile(r"^(?:-{5,}_?=_(?:Next)?Part_|--\d+-\d+-\d+=:\d+|Content-Type:)")  # some bizarre prefix that is found in some messages
SECTION_SUFFIX = re.compile(r"^Yahoo! Mail")
HYPHENS = re.compile(r"^[-_]+$")
FAKE_ID_MAX = 1000000


def get_body(email: str) -> str:
    # TODO: handle weird = truncations
    email = LINEBREAKS.sub("\n", email).strip()
    found: bool = False
    has_section: bool = False
    lines = []
    line_count = 0
    for line in email.split('\n'):
        line = line.strip()
        line_has_section = SECTION_PREFIX.match(line)
        line_has_suffix = SECTION_SUFFIX.match(line)
        if found and line_has_section and len(lines) == 0:
            found = False
            lines.clear()
            has_section = True
        elif found and has_section and (line_has_suffix or line_has_section):
            break
        elif found and line_has_section:
            found = False
            lines.clear()
            has_section = True
        elif found and len(line) == 0 and len(lines) == 0:
            pass
        elif found:
            lines.append(line)
        elif len(line) == 0:
            found = True

    # remove trailing "--------"s
    while len(lines) > 0 and HYPHENS.match(lines[-1]):
        lines = lines[:-1]

    output = html.unescape('\n'.join(lines).strip())
    if len(output) == 0:
        with open('possible_errors.txt', 'a', encoding='UTF-8') as f:
            f.write(email + "\n$%$%$%$%$%$%$%$%$%$%\n")
    try:
        # some bodies are base64-encoded
        return base64.b64decode(output.replace('\n', ''), validate=True).decode('UTF-8')
    except Exception:
        return output


class Extractor:
    userless_ids: typing.Dict[str, int] = {}
    userless_id = 0

    def __init__(self, output_dir_base: typing.Optional[str] = None):
        self.output_dir_base: str = output_dir_base if output_dir_base is not None else "data"
        self.user_data: typing.Dict[int, typing.Dict[str, typing.Any]] = self.load_user_data()

    def load_user_data(self) -> typing.Dict[int, typing.Dict[str, typing.Any]]:
        data: typing.Dict[int, typing.Dict[str, typing.Any]] = {}
        data_dir = os.path.join(self.output_dir_base, "users")
        os.makedirs(data_dir, exist_ok=True)
        for file in os.listdir(data_dir):
            with open(os.path.join(data_dir, file), 'r') as file_data:
                json_data = json.load(file_data)
                json_data["knownAliases"] = set(json_data["knownAliases"])
                json_data["knownGroups"] = set(json_data["knownGroups"])
                user_id: int = int(file.split('.')[0])
                data[user_id] = json_data
                if user_id < FAKE_ID_MAX:
                    self.userless_ids[json_data['knownAliases'][0]] = user_id
        return data

    def save_user_data(self):
        for user_id, user_data in self.user_data.items():
            user_data["knownAliases"] = list(set(user_data["knownAliases"]))
            user_data["knownGroups"] = list(set(user_data["knownGroups"]))
            with open(os.path.join(self.output_dir_base, "users", f"{user_id}.json"), 'w') as f:
                json.dump(user_data, f)

    def run(self, input_path: typing.Optional[str] = None):
        input_path = input_path if input_path is not None else "archives"
        for filename in os.listdir(input_path):
            group = filename.split('.')[0]
            filename = os.path.join(input_path, filename)
            group_output_dir = os.path.join(self.output_dir_base, "groups", group)
            with open(filename, 'rb') as stream:
                for record in ArchiveIterator(stream):
                    self.process_record(record, group, group_output_dir)
        self.save_user_data()

    def _next_id(self) -> int:
        self.userless_id += 1
        return self.userless_id

    def get_userless_id(self, alias: str) -> int:
        if alias not in self.userless_ids:
            self.userless_ids[alias] = self._next_id()
        return self.userless_ids[alias]

    def process_record(self, record, group: str, group_output_dir: str):
        if record.rec_type != 'resource':
            return
        match = MESSAGE_ID.match(record.rec_headers.get_header('WARC-Target-URI'))
        if not match:
            return

        message_id = match.group(1)
        os.makedirs(group_output_dir, exist_ok=True)
        file_content = record.content_stream().read()
        data = json.loads(file_content)

        alias: str = data['authorName'] if data['authorName'] else html.unescape(data['from'])  # TODO: get "X-Sender" from email headers?
        user_id: int = data['userId'] if data['userId'] != 0 else self.get_userless_id(alias)
        if user_id in self.user_data:
            aliases: set[str] = self.user_data[user_id]['knownAliases']
            aliases.add(alias)
        else:
            self.user_data[user_id] = {
                "userName": data['profile'] if 'profile' in data else None,
                "knownAliases": {alias},
                "knownGroups": {group},
                "id": user_id,
                "fakeAccount": user_id < 1000000
            }

        with open(os.path.join(group_output_dir, message_id + ".json"), 'w', encoding='UTF-8') as output:
            json.dump({
                "id": int(message_id),
                "subject": html.unescape(data['subject']) if 'subject' in data else None,
                "authorId": user_id,
                "alias": alias,
                "postDate": data['postDate'],
                "body": get_body(data['rawEmail']),
                "nextInTime": data['nextInTime']
            }, output, separators=(',', ':'))


if __name__ == '__main__':
    _args = sys.argv[1:]
    _output = _args[0] if len(_args) > 0 else None
    _input = _args[1] if len(_args) > 1 else None
    Extractor(_output).run(_input)
