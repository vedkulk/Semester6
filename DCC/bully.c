#include <stdio.h>
#include <unistd.h>

#define MAX 100

int processes[MAX];      
int n;                   
int coordinator;         
int winner;             

// Function to find the highest process ID (initial coordinator)
int findHighest() {
    int highest = processes[0];
    for (int i = 1; i < n; i++) {
        if (processes[i] > highest) {
            highest = processes[i];
        }
    }
    return highest;
}

// Function to send an election message
void sendMessage(int from, int to) {
    printf("\nProcess %d sends ELECTION message to Process %d", from, to);
    sleep(1);
}

// Function to send an OK message
void sendOK(int from, int to) {
    printf("\nProcess %d sends OK message to Process %d", from, to);
    sleep(1);
}

// Function to start an election, ensuring correct order
void startElection(int initiator) {
    int higherProcesses[MAX];
    int higherCount = 0;

    // Step 1: Collect all higher process IDs (excluding the coordinator)
    for (int i = 0; i < n; i++) {
        if (processes[i] > initiator) {
            higherProcesses[higherCount++] = processes[i];
        }
    }

    // Step 2: Sort the higher process IDs in ascending order
    for (int i = 0; i < higherCount - 1; i++) {
        for (int j = i + 1; j < higherCount; j++) {
            if (higherProcesses[i] > higherProcesses[j]) {
                int temp = higherProcesses[i];
                higherProcesses[i] = higherProcesses[j];
                higherProcesses[j] = temp;
            }
        }
    }

    // Step 3: Send election messages in order
    if (higherCount == 0) {
        winner = initiator;  // No higher processes, initiator wins
        return;
    }

    // Process sends messages to all higher processes
    for (int i = 0; i < higherCount; i++) {
        sendMessage(initiator, higherProcesses[i]);
    }

    // Step 4: Process should collect all the OK messages from higher processes
    int maxProcess = initiator;
    for (int i = 0; i < higherCount; i++) {
        // Only send OK if the process is not the highest
        if (higherProcesses[i] != coordinator) {
            sendOK(higherProcesses[i], initiator); // Every higher process sends OK
            if (higherProcesses[i] > maxProcess) {
                maxProcess = higherProcesses[i]; // Update the winner if needed
            }
        }
    }

    winner = maxProcess;  // Set the winner to the highest process after the election
}

// Function for a process to send election message and handle OK responses
void handleElection(int initiator) {
    // This function should be invoked by every process that receives an election message
    // It checks if it can start its own election

    int higherProcesses[MAX];
    int higherCount = 0;
    
    // Find all higher processes that can initiate an election
    for (int i = 0; i < n; i++) {
        if (processes[i] > initiator) {
            higherProcesses[higherCount++] = processes[i];
        }
    }

    if (higherCount > 0) {
        // If higher processes exist, the process will send election messages
        for (int i = 0; i < higherCount; i++) {
            sendMessage(initiator, higherProcesses[i]);
        }
    } else {
        // If no higher processes, this process is the winner
        winner = initiator;
    }
}

int main() {
    printf("Enter number of processes: ");
    scanf("%d", &n);
    
    printf("Enter process IDs:\n");
    for (int i = 0; i < n; i++) {
        scanf("%d", &processes[i]);
    }
    
    coordinator = findHighest();
    printf("\nProcess %d has been declared coordinator.", coordinator);
    
    int initiator;
    printf("\nEnter the initiator process ID: ");
    scanf("%d", &initiator);
    
    printf("\n--- Election started by Process %d ---", initiator);
    
    startElection(initiator);
    
    printf("\nProcess %d becomes the winner.\n", winner);
    
    return 0;
}
