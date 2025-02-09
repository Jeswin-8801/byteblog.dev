# Setting up Email Service for Google Account

> Goto: https://myaccount.google.com/apppasswords

- Once created, it will show up like so:
![app_password](image-3.png)

# Setting up Oauth2 Credentials

Follow the below steps for setting up Oauth2 credentials for *`GitHub`* and *`Google`* providers.

## GitHub

> Goto: https://github.com/settings/developers

![select_project](image.png)

![configure](image-1.png)

![additional_configure](image-4.png)

## Google

> Goto: https://console.cloud.google.com/auth/clients

- create client and setup the required configs

![configure](image-2.png)

---

> [!Note]
> Modify [](client/src/app/common/app.constants.ts) only if needed