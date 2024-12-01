# Use a lightweight Node.js image as the base
FROM node:18-alpine

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the Vite app
RUN npm run build

# Install a lightweight HTTP server globally
RUN npm install -g serve

# Set the default command to serve the built app
CMD ["serve", "-s", "dist"]

# Expose the default port used by `serve`
EXPOSE 3000
