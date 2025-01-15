#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <netinet/in.h>
#define PORT 9090

float calculate(float num1, float num2, char operator) {
    float result;
    switch (operator) {
        case '+':
            result = num1 + num2;
            break;
        case '-':
            result = num1 - num2;
            break;
        case '*':
            result = num1 * num2;
            break;
        case '/':
            if (num2 != 0) {
                result = num1 / num2;
            } else {
                printf("Error: Division by zero\n");
                result = 0;
            }
            break;
        default:
            printf("Invalid operator\n");
            result = 0;
    }
    return result;
}

int main(int argc, char const *argv[]) {
    int server_fd, new_socket;
    ssize_t valread;
    struct sockaddr_in address;
    int opt = 1;
    socklen_t addrlen = sizeof(address);
    char buffer[1024] = {0};
    char result_message[1024] = {0};

    if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("socket failed");
        exit(EXIT_FAILURE);
    }

    if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt))) {
        perror("setsockopt");
        exit(EXIT_FAILURE);
    }

    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(PORT);

    if (bind(server_fd, (struct sockaddr *)&address, sizeof(address)) < 0) {
        perror("bind failed");
        exit(EXIT_FAILURE);
    }

    if (listen(server_fd, 3) < 0) {
        perror("listen failed");
        exit(EXIT_FAILURE);
    }

    if ((new_socket = accept(server_fd, (struct sockaddr *)&address, &addrlen)) < 0) {
        perror("accept failed");
        exit(EXIT_FAILURE);
    }

    valread = read(new_socket, buffer, sizeof(buffer) - 1);
    buffer[valread] = '\0';  

    float num1, num2;
    char operator;
    sscanf(buffer, "%f %c %f", &num1, &operator, &num2);

    float result = calculate(num1, num2, operator);

    sprintf(result_message, "Result: %.2f", result);

    send(new_socket, result_message, strlen(result_message), 0);

    printf("Result sent to client: %s\n", result_message);

    close(new_socket);
    close(server_fd);
    return 0;
}
