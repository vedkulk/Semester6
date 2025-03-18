#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <netinet/in.h>

#define PORT 9090

// Function to perform the requested calculation
float calculate(float num1, float num2, char operator) {
    switch (operator) {
        case '+': return num1 + num2;
        case '-': return num1 - num2;
        case '*': return num1 * num2;
        case '/': return (num2 != 0) ? num1 / num2 : 0;  // Prevent division by zero
        default: return 0;
    }
}

int main() {
    int server_fd, new_socket;
    struct sockaddr_in address;
    socklen_t addrlen = sizeof(address);
    char buffer[1024] = {0};
    char result_message[1024];

    // Create socket
    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    // Set socket options
    int opt = 1;
    if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt)) < 0) {
        perror("setsockopt failed");
        exit(EXIT_FAILURE);
    }

    // Define server address
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    // Bind socket
    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0) {
        perror("Bind failed");
        exit(EXIT_FAILURE);
    }

    // Listen for connections
    if (listen(server_fd, 3) < 0) {
        perror("Listen failed");
        exit(EXIT_FAILURE);
    }

    printf("Server listening on port %d...\n", PORT);

    while (1) {  // Keep server running for multiple clients
        // Accept a connection
        if ((new_socket = accept(server_fd, (struct sockaddr *)&address, &addrlen)) < 0) {
            perror("Accept failed");
            exit(EXIT_FAILURE);
        }

        // Read client's message
        int valread = read(new_socket, buffer, sizeof(buffer) - 1);
        buffer[valread] = '\0';

        printf("Received: %s\n", buffer);

        // Parse the input and perform calculation
        float num1, num2;
        char operator;
        sscanf(buffer, "%f %c %f", &num1, &operator, &num2);
        float result = calculate(num1, num2, operator);

        // Send result back to client
        sprintf(result_message, "Result: %.2f", result);
        send(new_socket, result_message, strlen(result_message), 0);

        printf("Sent to client: %s\n", result_message);

        close(new_socket);
    }

    close(server_fd);
    return 0;
}
