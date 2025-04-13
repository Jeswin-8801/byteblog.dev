FROM node:22-alpine

RUN npm install -g pnpm serve

WORKDIR /app
COPY package.json pnpm-lock.yaml .

RUN --mount=type=cache,id=pnpm,target=/root/.local/share/pnpm/store pnpm fetch --frozen-lockfile
RUN --mount=type=cache,id=pnpm,target=/root/.local/share/pnpm/store pnpm install --frozen-lockfile

COPY . .
RUN pnpm build
RUN mv dist/browser/index.html dist/
RUN mv dist/browser dist/byteblog

# Run application
ENTRYPOINT ["sh", "-c", "serve -s dist -l $ANGULAR_DOCKER_PORT"]
