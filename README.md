![Showcase Card](https://github.com/user-attachments/assets/2f2b1fa5-268b-42e8-a570-61916ab6c8a7)


<div align="center">

# byteblog.dev

My personal tech blog-built using [Spring Boot](https://spring.io/projects/spring-boot), [Angular](https://angular.dev/) and [Tailwind](https://tailwindcss.com/).

</div>

---

<p align="center">
<img src="https://github.com/user-attachments/assets/dee1f8a0-8d17-47a8-8380-80da3592fe8c" alt="spring-boot" style="width: 100px; height: 100px"> <img src="https://github.com/user-attachments/assets/cd4d9a83-d600-488d-95e0-cb6b3f00acd6" alt="postgres" style="width: 100px; height: 100px"> <img src="https://github.com/user-attachments/assets/83dff3f3-0d67-4f09-892f-6be7c8dec65c" alt="angular" style="width: 100px; height: 100px">
</p>

# Technology Stack

This is a list of the various technologies used to build this website:

| Category               | Technology Name                                                                                    |
| ---------------------- | -------------------------------------------------------------------------------------------------- |
| Backend                | [Spring Boot](https://spring.io/projects/spring-boot)                                              |
| Database               | [Postgres](https://www.postgresql.org/)                                                            |
| Frontend               | [Angular](https://angular.dev/)                                                                    |
| Styling                | [Tailwind](https://tailwindcss.com)                                                                |
| Static File Storage    | [Minio](https://min.io/)                                                                           |
| Content                | [MDX](https://mdxjs.com/)                                                                          |
| Packaging & Deployment | [Docker](https://www.docker.com/)                                                                  |

# Features

- Token authentication with refresh token blacklisting
- Oauth2 authentication for Google and Github accounts
- Mail-based verification for new account registration
- Support for posting blogs through `.mdx` files along with support for markdown rendering
- Containerization of services for instant setup and easy CI/CD.
- Adds support for posting blog comments and also allows for infinite nesting of comments.
- Disables access to certain pages and services for non authorized users.

# Setup

In the base directory run

`docker-compose up -d`

> Wait a few minutes until all the containers are up and running

### Access the website at http://localhost:11001/ once `nginx` is up

> http://localhost:8001/ for `pgadmin`'
> </br>
> http://localhost:9001/browser/byte-blog-bucket for accessing all static files stored in `minio`

</br>

> [!Note]
> Make sure you update all secrets for mail and oauth2 to enable these services
