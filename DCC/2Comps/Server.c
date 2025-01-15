#include <arpa/inet.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>
#include <stdlib.h>

#define PORT 9090  
#define BACKLOG 5  // Maximum number of connections in the listen queue

int main() {
    int server_fd, new_socket, valread;
    struct sockaddr_in address;
    char buffer[1024] = {0};
    char response[] = "Message received!";
    
    // Create socket
    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
        perror("Socket failed");
        exit(EXIT_FAILURE);
    }

    // Set server address
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY; // Listen on all available interfaces
    address.sin_port = htons(PORT);

    // Bind the socket to the IP address and port
    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0) {
        perror("Bind failed");
        exit(EXIT_FAILURE);
    }

    // Listen for incoming connections
    if (listen(server_fd, BACKLOG) < 0) {
        perror("Listen failed");
        exit(EXIT_FAILURE);
    }

    printf("Server listening on port %d...\n", PORT);

    // Accept incoming client connections
    while (1) {
        int addrlen = sizeof(address);
        if ((new_socket = accept(server_fd, (struct sockaddr *)&address, (socklen_t*)&addrlen)) < 0) {
            perror("Accept failed");
            exit(EXIT_FAILURE);
        }

        printf("New connection established.\n");

        // Handle client communication
        while (1) {
            valread = read(new_socket, buffer, sizeof(buffer) - 1);
            if (valread > 0) {
                buffer[valread] = '\0';  // Null-terminate the received string
                printf("Client: %s\n", buffer);

                // Send response to client
                send(new_socket, response, strlen(response), 0);
                printf("Response sent to client: %s\n", response);
            } else {
                printf("Client disconnected or read error.\n");
                break;
            }
        }

        // Close the client socket after the interaction
        close(new_socket);
    }

    // Close the server socket when done
    close(server_fd);
    return 0;
}
