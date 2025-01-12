FROM node:latest

RUN mkdir -p /usr/src/cn_middleware
WORKDIR /usr/src/cn_middleware

COPY package.json /usr/src/cn_middleware
RUN apt-get update
RUN apt-get install -y build-essential
RUN npm install --build-from-source

RUN npm install -g typescript

COPY . /usr/src/cn_middleware

# Build the bot
RUN tsc

# Start the bot.
CMD ["node", "build/index.js"]