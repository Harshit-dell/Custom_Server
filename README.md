# Simple Java HTTP Server for NetGauge


This is a side project — a custom-built HTTP server in Java designed to handle basic `upload` and `download` requests.  
It can be used with a speed testing tool like **NetGauge** to measure network performance.

## Features

- Handles HTTP `GET` requests
- `/upload` endpoint to accept data for upload speed testing
- `/download` endpoint to send large data for download speed testing
- `/shutdown` endpoint to stop the server
- `/home` root path shows a simple welcome message

## Purpose

The server is meant to act as a local backend endpoint for testing upload and download speeds.  
It does not persist data — it just reads and responds to measure transfer speed.

## Usage

1. Clone the repository
2. Run the `Main.java` file
3. Send HTTP requests to `localhost:8080` using any client (like browser or custom tester)

Example endpoints:
- `GET /home` – Welcome
- `GET /download` – Start download
- `POST /upload` – Send upload data
- `GET /shutdown` – Gracefully shut down the server

## Note

This project was created for learning and internal testing purposes only.
