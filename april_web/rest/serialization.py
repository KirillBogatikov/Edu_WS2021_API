from rest_framework.fields import empty
from rest_framework.serializers import ModelSerializer

from rest.models import User, Auth


class AuthSerializer(ModelSerializer):
    class Meta:
        model = Auth
        depth = 1
        fields = '__all__'

    def update(self, instance, validated_data):
        instance.login = validated_data.get('login', instance.login)
        instance.password = validated_data.get('password', instance.password)
        instance.save()
        return instance


class UserSerializer(ModelSerializer):
    def __init__(self, instance=None, data=empty, **kwargs):
        self.hide_password = kwargs.pop("hide_password", False)
        ModelSerializer.__init__(self, instance=instance, data=data, **kwargs)

    class Meta:
        model = User
        depth = 1
        fields = '__all__'

    def get_fields(self):
        fields = ModelSerializer.get_fields(self)
        if self.hide_password:
            print(fields['auth'])
            #fields.pop("password")


        return fields

    def update(self, instance, validated_data):
        instance.auth.__dict__ = validated_data.get('auth', instance.auth.__dict__)
        instance.personal.__dict__ = validated_data.get('personal', instance.personal.__dict__)
        instance.save()
        return instance
