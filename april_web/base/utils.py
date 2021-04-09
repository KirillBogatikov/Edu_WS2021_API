from hashlib import sha256


def hex_hash(text):
    return sha256(text.encode('utf-8')).hexdigest()