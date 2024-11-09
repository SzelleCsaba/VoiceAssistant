# Step 1: Specify the base image
FROM python:3.9-slim

# Step 2: Set the working directory in the container
WORKDIR /app

# Step 3: Copy the requirements file and install dependencies
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# Step 4: Copy the application code to the container
COPY . .

# Step 5: Expose the port that Waitress will listen on
EXPOSE 8080

# Step 6: Define the command to run the app with Waitress
CMD ["python", "run_server.py"]
