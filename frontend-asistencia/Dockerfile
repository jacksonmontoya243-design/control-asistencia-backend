# Stage 1: Build React App
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
# Copiamos la configuración de Nginx para manejar el ruteo de React y el proxy
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Certificado autofirmado por defecto para que HTTPS siempre funcione.
# En producción se puede sobreescribir montando un volumen sobre /etc/nginx/ssl.
RUN mkdir -p /etc/nginx/ssl && \
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /etc/nginx/ssl/server.key \
    -out /etc/nginx/ssl/server.crt \
    -subj "/C=CO/ST=Bogota/L=Bogota/O=AsistenciaPro/CN=localhost"
EXPOSE 80 443
CMD ["nginx", "-g", "daemon off;"]