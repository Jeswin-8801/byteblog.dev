# Stage 1
FROM node:20 AS build
WORKDIR /app
COPY . .
RUN npm ci
RUN npm run build

# Stage 2
FROM nginx:1.27.2-alpine

# Copies the custom NGINX configuration file you've written in your project to the right location in your Docker image.
COPY nginx.conf /etc/nginx/nginx.conf

COPY --from=build /app/dist/client/browser /usr/share/nginx/html

# This makes port 8080 available to your host machine.
EXPOSE 8080

# This is starting NGINX and making sure it stays running. The "-g 'daemon off;'" part is grabbing the global configuration to modify it.
CMD ["nginx", "-g", "daemon off;"]
