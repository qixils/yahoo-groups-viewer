import json
import sys
import typing
import re
import os

from warcio.archiveiterator import ArchiveIterator

MESSAGE_ID = re.compile(r"^org\.archive\.yahoogroups:v1/group/[a-z_]+/message/(\d+)/raw$")
LINEBREAKS = re.compile(r"\r?\n")


def get_body(email: str) -> str:
    email = LINEBREAKS.sub("\n", email).strip()
    found: bool = False
    lines = []
    for line in email.split('\n'):
        if found:
            lines.append(line)
        elif len(line) == 0:
            found = True
    return '\n'.join(lines)


def run(params: typing.List[str]):
    if len(params) == 0:
        print("No file was specified")
        exit(1)
    filename = params[0]
    if not os.path.exists(filename):
        print(f"Could not find a file by the name '{filename}'")
        exit(1)
    output_dir_base = "groups"
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
            data = json.loads(record.content_stream().read())
            with open(os.path.join(output_dir, message_id + ".json"), 'w') as output:
                json.dump({
                    "subject": data['subject'] if 'subject' in data else None,
                    "displayName": data['from'],
                    "realName": data['authorName'],
                    "userName": data['profile'] if 'profile' in data else None,
                    "userId": data['userId'],
                    "postDate": data['postDate'],
                    "replyTo": data['replyTo'],
                    "body": get_body(data['rawEmail']),
                    "nextInTime": data['nextInTime']
                }, output, separators=(',', ':'))


if __name__ == '__main__':
    run(sys.argv[1:])
