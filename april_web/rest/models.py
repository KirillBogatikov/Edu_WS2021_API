import enum

from django.db.models import Model, CharField, Manager, ForeignKey, CASCADE, IntegerField, TextField

from rest.validation import IdLength, LoginRule, PasswordRule, NameRule, PhoneRule, EmailRule


class Role(enum.Enum):
    User = 0
    Admin = 1


class IssueStatus(enum.Enum):
    Created = 0
    Processing = 1
    Ready = 2


class Auth(Model):
    id = CharField(max_length=IdLength, primary_key=True)
    login = CharField(max_length=LoginRule.max_length, null=False)
    password = CharField(max_length=PasswordRule.max_length, null=False)
    role = IntegerField()
    objects = Manager()


class PersonalData(Model):
    id = CharField(max_length=IdLength, primary_key=True)
    first_name = CharField(max_length=NameRule.max_length, null=False)
    last_name = CharField(max_length=NameRule.max_length, null=False)
    patronymic = CharField(max_length=NameRule.max_length, null=True, default="")
    phone = CharField(max_length=PhoneRule.max_length, null=False)
    email = CharField(max_length=EmailRule.max_length, null=False)
    objects = Manager()


class User(Model):
    id = CharField(max_length=IdLength, primary_key=True)
    auth = ForeignKey(Auth, on_delete=CASCADE)
    personal = ForeignKey(PersonalData, on_delete=CASCADE)
    objects = Manager()


class Photo(Model):
    """
    Note: Storing images in database is VERY VERY VERY bad practice, i know, but why we haven't NoSQL storage?
    Why only MySQL and prebuilt SQLite? I WANT PGSQL + MONGO!
    """
    id = CharField(max_length=IdLength, primary_key=True)
    bytes = TextField()  # base64-encoded image bytes
    objects = Manager()


class Issue(Model):
    id = CharField(max_length=IdLength, primary_key=True)
    pet_photo = ForeignKey(Photo, on_delete=CASCADE, related_name="pet_photo_fk")
    result_photo = ForeignKey(Photo, on_delete=CASCADE, related_name="result_photo_fk")
    status = IntegerField()
    objects = Manager()
