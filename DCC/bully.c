#include <stdio.h>
#include <unistd.h>

#define MAX 100

int processIDs[MAX];
int numProcesses;
int coordinator;

void printname()
{
    printf("hi this is me!!");
}
int findMaxID()
{
    int maxID = processIDs[0];
    for (int i = 1; i < numProcesses; i++)
    {
        if (processIDs[i] > maxID)
        {
            maxID = processIDs[i];
        }
    }
    return maxID;
}

void sendElectionMessage(int from, int to)
{
    printf("\nProcess %d sends ELECTION message to Process %d", from, to);
    sleep(1);
}

void sendOKMessage(int from, int to)
{
    printf("\nProcess %d sends OK message to Process %d", from, to);
    sleep(1);
}

void startElection(int initiator)
{
    printf("\n--- Election started by Process %d ---", initiator);
    int deadProcess = coordinator;
    int activeProcesses[MAX];
    int activeCount = 0;

    for (int i = 0; i < numProcesses; i++)
    {
        if (processIDs[i] != deadProcess)
        {
            activeProcesses[activeCount++] = processIDs[i];
        }
    }

    int initiatorIndex = -1;
    for (int i = 0; i < activeCount; i++)
    {
        if (activeProcesses[i] == initiator)
        {
            initiatorIndex = i;
            break;
        }
    }

    // Step 1: Send election messages to all higher processes
    for (int i = 0; i < numProcesses; i++)
    {
        if (processIDs[i] > initiator)
        {
            sendElectionMessage(initiator, processIDs[i]);
        }
    }

    // Step 2: Wait for OK messages from active higher processes
    int highestActive = -1;
    for (int i = 0; i < numProcesses; i++)
    {
        if (processIDs[i] > initiator && processIDs[i] != deadProcess)
        {
            sendOKMessage(processIDs[i], initiator);
            highestActive = processIDs[i];
        }
    }

    // Step 3: If a higher active process exists, it starts a new election
    if (highestActive != -1)
    {
        startElection(highestActive);
    }
    else
    {
        printf("\nProcess %d is the new coordinator\n", initiator);
    }
}

int main()
{
    printf("Enter number of processes: ");
    scanf("%d", &numProcesses);

    printf("Enter process IDs:\n");
    for (int i = 0; i < numProcesses; i++)
    {
        scanf("%d", &processIDs[i]);
    }

    coordinator = findMaxID();
    printf("\nProcess %d is the current coordinator \n", coordinator);
    printf("%d is fail", coordinator);

    int initiator;
    printf("\nEnter the initiator process ID: ");
    scanf("%d", &initiator);

    startElection(initiator);

    printname();

    return 0;
}
