import base64
import binascii
import html
import json
import sys
import typing
import re
import os
from typing import Union

from warcio.archiveiterator import ArchiveIterator

MESSAGE_ID = re.compile(r"^org\.archive\.yahoogroups:v1/group/[a-z_]+/message/(\d+)/raw$")
LINEBREAKS = re.compile(r"\r?\n")
SECTION_PREFIX = re.compile(r"^(?:-{5,}_?=_(?:Next)?Part_|--\d+-\d+-\d+=:\d+|Content-Type: )")  # some bizarre prefix that is found in some messages
SECTION_SUFFIX = re.compile(r"^ ?Yahoo! Mail")
HYPHENS = re.compile(r"^[-_]+$")


def get_body(email: str) -> str:
    # TODO: still imperfect; needs more tuning
    email = LINEBREAKS.sub("\n", email).strip()
    found: bool = False
    has_section: bool = False
    lines = []
    for line in email.split('\n'):
        line_has_section = SECTION_PREFIX.match(line)
        line_has_suffix = SECTION_SUFFIX.match(line)
        if found and (line_has_suffix or (has_section and line_has_section)) and len(lines) > 0:
            break
        elif found and line_has_section:
            found = False
            lines.clear()
            has_section = True
        elif found:
            lines.append(line)
        elif len(line) == 0:
            found = True

    # remove trailing "--------"s
    if len(lines) > 0 and HYPHENS.match(lines[-1]):
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


def run(params: typing.List[str]):
    if len(params) == 0:
        print("No file was specified")
        exit(1)
    filename = params[0]
    if not os.path.exists(filename):
        print(f"Could not find a file by the name '{filename}'")
        exit(1)
    output_dir_base = "groups" if len(params) == 1 else params[1]
    output_dir = os.path.join(output_dir_base, filename.split('.')[0])
    with open(params[0], 'rb') as stream:
        for record in ArchiveIterator(stream):
            if record.rec_type != 'resource':
                continue
            match = MESSAGE_ID.match(record.rec_headers.get_header('WARC-Target-URI'))
            if not match:
                continue
            message_id = match.group(1)
            if not os.path.exists(output_dir_base):
                os.mkdir(output_dir_base)
            if not os.path.exists(output_dir):
                os.mkdir(output_dir)
            file_content = record.content_stream().read()
            data = json.loads(file_content)
            with open(os.path.join(output_dir, message_id + ".json"), 'w', encoding='UTF-8') as output:
                json.dump({
                    "id": int(message_id),
                    "subject": data['subject'] if 'subject' in data else None,
                    "displayName": html.unescape(data['from']),
                    "realName": data['authorName'],
                    "userName": data['profile'] if 'profile' in data else None,
                    "userId": data['userId'],
                    "postDate": data['postDate'],
                    "body": get_body(data['rawEmail']),
                    "nextInTime": data['nextInTime']
                }, output, separators=(',', ':'))


if __name__ == '__main__':
    run(sys.argv[1:])
