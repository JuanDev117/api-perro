FROM nginx:1.27-alpine

ENV PORT=10000
ENV API_BASE_URL=http://localhost:8081

# Copiar plantilla de configuracion de Nginx
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

# Copiar script de inicializacion para inyectar variables de entorno en tiempo de ejecucion
COPY docker-entrypoint.d/40-generate-config.sh /docker-entrypoint.d/40-generate-config.sh

# Asegurar terminaciones de linea Unix (LF) y permisos de ejecucion
RUN sed -i 's/\r$//' /docker-entrypoint.d/40-generate-config.sh && \
    chmod +x /docker-entrypoint.d/40-generate-config.sh

# Copiar archivos estaticos de la aplicacion
COPY index.html app.js styles.css config.js /usr/share/nginx/html/

EXPOSE 10000

# El entrypoint oficial de nginx procesara /etc/nginx/templates y ejecutara /docker-entrypoint.d/*.sh
