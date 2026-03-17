# Build Angular
FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm install

COPY . .
RUN npx ng build -c production

# Run (Express + proxy /api vers le back)
FROM node:20-alpine
WORKDIR /app

RUN apk add --no-cache dumb-init

# Dépendances du serveur (server.cjs)
COPY package-server.json ./package.json
RUN npm install

COPY server.cjs ./
COPY --from=build /app/dist ./dist

EXPOSE 4300
ENV PORT=4300
ENV API_TARGET=http://back:8080

ENTRYPOINT ["dumb-init", "--"]
CMD ["node", "server.cjs"]
