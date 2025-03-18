#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define PORT 9090

int main() {
    int sock;
    struct sockaddr_in serv_addr;
    char buffer[1024] = {0};
    char input[1024];

    // Create socket
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(PORT);

    // Convert IP address to binary and store in serv_addr
    if (inet_pton(AF_INET, "127.0.0.1", &serv_addr.sin_addr) <= 0) {
        perror("Invalid address");
        exit(EXIT_FAILURE);
    }

    // Connect to server
    if (connect(sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr)) < 0) {
        perror("Connection failed");
        exit(EXIT_FAILURE);
    }

    // Get user input
    printf("Enter operation (e.g., 12.5 + 7.5): ");
    fgets(input, sizeof(input), stdin);
    input[strcspn(input, "\n")] = 0;  // Remove newline character

    // Send input to server
    send(sock, input, strlen(input), 0);

    // Receive response from server
    int valread = read(sock, buffer, sizeof(buffer) - 1);
    buffer[valread] = '\0';

    printf("Server response: %s\n", buffer);

    close(sock);
    return 0;
}
