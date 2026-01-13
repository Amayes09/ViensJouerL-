#!/bin/bash
set -e

# Wait for PostgreSQL to be ready
until PGPASSWORD=admin psql -h starter-db -U admin -d starterdb -c '\q'; do
  echo 'Waiting for PostgreSQL...'
  sleep 1
done
echo 'PostgreSQL is ready!'

# Create a temporary asadmin commands file
cat > /tmp/setup-commands.txt << 'EOF'
create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGSimpleDataSource --restype javax.sql.DataSource --property user=admin:password=admin:servername=starter-db:portNumber=5432:databaseName=starterdb PostgreSQLPool
create-jdbc-resource --connectionpoolid PostgreSQLPool jdbc/starter__pm
EOF

# Start Payara with asadmin for setup and then run normally
/opt/payara/bin/asadmin start-domain --verbose &
PAYARA_PID=$!

# Give Payara time to boot up
sleep 30

# Execute setup commands
/opt/payara/bin/asadmin -u admin -p admin -I false < /tmp/setup-commands.txt || true

# Deploy the WAR file
/opt/payara/bin/asadmin -u admin -p admin deploy /opt/payara/deployments/starter.war || true

# Keep Payara running
wait $PAYARA_PID
