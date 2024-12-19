# Base image with Tomcat 10 and JDK 17
FROM tomcat:10-jdk17

# Set the working directory inside the container
WORKDIR /app

# Copy the project files to the container
COPY . /app

# Convert script to Unix format (optional for Windows users)
RUN apt-get update && apt-get install -y dos2unix
RUN dos2unix /app/deploy.sh

# Make the deploy script executable
RUN chmod +x /app/deploy.sh

# Run the deployment script with Bash
RUN /bin/bash /app/deploy.sh

# Move the generated WAR file to the Tomcat webapps directory
RUN mv /app/fournisseur-identite.war /usr/local/tomcat/webapps/

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
