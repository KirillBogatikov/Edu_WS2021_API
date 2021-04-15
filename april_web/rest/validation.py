import re


class ValidationRule:
    def __init__(self, min_length=1, max_length=128, regex=r'.*'):
        self.min_length = min_length
        self.max_length = max_length
        self.regex = regex

    def validate(self, value, name="Value"):
        if value is None:
            raise ValueError("{} is None".format(name))

        if len(value) < self.min_length:
            raise ValueError("{} too short".format(name))

        if len(value) > self.max_length:
            raise ValueError("{} too long".format(name))

        if not re.match(self.regex, value, re.IGNORECASE):
            raise ValueError("{} contains incorrect characters".format(name))

        # OK


IdLength = 36  # UUID4 length
HashLength = 256  # SHA-256 hash length
LoginRule = ValidationRule(4, 32, r"^[a-z0-9_\-]+$")
PasswordRule = ValidationRule(8, regex=r"^[a-z0-9_\-!@#$%^&*~]+$")
NameRule = ValidationRule(regex=r"^[a-zа-яё ]+$")
EmailRule = ValidationRule(3, regex=r"^[a-z0-9_\-]+@[a-z0-9_\-]+[.][a-z0-9_\-]+$")
PhoneRule = ValidationRule(1, regex=r"^[+*]?[0-9\-() ]+$")
