FROM payara/server-full:latest

# Install postgresql client for database readiness check
RUN apt-get update && apt-get install -y postgresql-client && rm -rf /var/lib/apt/lists/*

# Copy the WAR file for deployment
COPY target/starter.war /opt/payara/deployments/

# Copy PostgreSQL JDBC driver
COPY postgresql-42.6.0.jar /opt/payara/appserver/glassfish/domains/domain1/lib/

# Copy initialization script
COPY init-payara.sh /opt/payara/init-payara.sh
RUN chmod +x /opt/payara/init-payara.sh

EXPOSE 8080 4848

CMD ["/opt/payara/init-payara.sh"]
