import string
from datetime import datetime, timedelta
from hashlib import sha256
from random import choice

from django.conf import settings


def hex_hash(password):
    return sha256(password.encode('utf-8')).hexdigest()


def random_token(length):
    letters = string.ascii_lowercase
    return ''.join(choice(letters) for i in range(length))


def set_cookie(response, key, value, days_expire=7):
    max_age = days_expire * 24 * 60 * 60
    expires = datetime.strftime(
        datetime.utcnow() + timedelta(seconds=max_age),
        "%a, %d-%b-%Y %H:%M:%S GMT",
    )
    response.set_cookie(
        key,
        value,
        max_age=max_age,
        expires=expires,
        domain=settings.SESSION_COOKIE_DOMAIN,
        secure=settings.SESSION_COOKIE_SECURE or None,
    )
