from rest.auth import JWToken

token = JWToken("1234")
e_token = token.encode()
print(e_token)

d_token = JWToken.decode(e_token)
print(d_token.user_id)
