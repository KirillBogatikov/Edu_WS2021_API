import enum
import re


class ValidationModel:
    def __init__(self, min, max, regex, message):
        self.min_length = min
        self.max_length = max
        self.regex = re.compile(regex)
        self.message = message

    def is_valid(self, value):
        return len(self.regex.findall(value)) > 0


IDLength = 36  # uuid-4 length
Login = ValidationModel(4, 32, r'(\w|[_@#$])+', 'a-z, A-Z, 0-9, _, @, #, $')
Password = ValidationModel(4, 32, r'(\w|[а-яА-Я_])+', 'a-z, A-Z, а-я, А-Я, 0-9, _')
HashLength = 64  # sha-256 hash length
Name = ValidationModel(1, 64, r'[a-zA-Zа-яА-Я \-]', 'a-z, A-Z, а-я, А-Я')
"""
    Note about phone numbers
    Most numbers starts with + and country code, i.e. +7(xxx)xxxxxxx - russian 'mobile' phone number
    Also, some countries use local numbers without + symbol, i.e. russian 'home' phone - xxx-xx-xx or xx-xx-xx
    Israel, for marking phone as advertisement use * instead of +
    Theoretically, number is not limited with length 
"""
Phone = ValidationModel(1, 128, r'[+*]?[0-9 \-()]+', '')
"""
    Note about emails
    Old libraries use hardcoded domains, i.e. gmail.com or *.com, but now
    companies can buy own domain. This regex is not perfect, but can basically check email validity.
"""
Email = ValidationModel(3, 256, r'\w+@\w+\.\w+', '')


class Role(enum.Enum):
    Client = 0
    Manager = 1
    Admin = 2
