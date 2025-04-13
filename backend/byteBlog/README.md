# Setting up Email Service for Google Account

> Goto: https://myaccount.google.com/apppasswords

- Once created, it will show up like so:
![app_password](https://github.com/user-attachments/assets/8dac396f-d45b-4624-8f0d-bbe381c9675c)


# Setting up Oauth2 Credentials

Follow the below steps for setting up Oauth2 credentials for *`GitHub`* and *`Google`* providers.

## GitHub

> Goto: https://github.com/settings/developers

![select_project](https://github.com/user-attachments/assets/688c39d3-04b9-440e-8762-e035f65c541b)

![configure](https://github.com/user-attachments/assets/936276e1-4b1f-43eb-ad86-f9c3cf68e375)

![additional_configure](https://github.com/user-attachments/assets/10c5388c-1827-4eba-b83e-0d6fa6f3b537)

## Google

> Goto: https://console.cloud.google.com/auth/clients

- create client and setup the required configs

![configure](https://github.com/user-attachments/assets/a7cda509-6d4a-41df-ae91-91e6a90241bf)

---

> [!Note]
> Modify [client/src/app/common/app.constants.ts](client/src/app/common/app.constants.ts) only if needed
