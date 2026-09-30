FROM node:24-alpine AS builder
WORKDIR /app
RUN apk add --no-cache python3 make g++
COPY package.json ./
RUN npm install --omit=dev

FROM node:24-alpine
RUN addgroup -S kesho && adduser -S kesho -G kesho
WORKDIR /app
COPY --from=builder /app/node_modules ./node_modules
COPY package.json ./
COPY src ./src

RUN mkdir -p /app/data && chown -R kesho:kesho /app/data
USER kesho

ENV NODE_ENV=production
EXPOSE 4000

CMD ["sh", "-c", "node src/seed.js && node src/index.js"]
