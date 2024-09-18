import { ArtNetController } from 'artnet-protocol/dist';
import { ArtDmx } from 'artnet-protocol/dist/protocol';
import { config } from 'dotenv';
import ws from 'ws';

config();

let wsClient: ws.WebSocket = null; // Define WebSocket client at the top level to allow access from anywhere

// Function to check if required environment variables are set
async function checkConfig() {
    const requiredEnvVars = ['ARTNET_BIND_IP', 'PLUGIN_HOST', 'PLUGIN_PORT', 'PLUGIN_PASSWORD'];
    const missingEnvVars = requiredEnvVars.filter(envVar => !process.env[envVar]);

    if (missingEnvVars.length > 0) {
        console.error(`Missing environment variables: ${missingEnvVars.join(', ')}`);
        process.exit(1);
    }
}

// Function to reconnect with exponential backoff
function reconnect(reconnectAttempts = 0) {
    const maxAttempts = 5;
    const reconnectDelay = Math.min(1000 * (2 ** reconnectAttempts), 30000); // Exponential backoff with a cap at 30s

    if (reconnectAttempts >= maxAttempts) {
        console.error('Max reconnect attempts reached. Exiting...');
        process.exit(1);
    }

    console.log(`Reconnecting in ${reconnectDelay / 1000} seconds...`);

    setTimeout(() => {
        console.log('Attempting to reconnect...');
        wsClient.terminate(); // Ensure the client is closed
        setupWebSocket(reconnectAttempts + 1); // Retry with incremented attempts
    }, reconnectDelay);
}

// Function to set up WebSocket connection and handle events
function setupWebSocket(reconnectAttempts = 0) {
    const ws_host = process.env.PLUGIN_HOST;
    const ws_port = parseInt(process.env.PLUGIN_PORT);
    const password = process.env.PLUGIN_PASSWORD;

    // Add the password to the WebSocket handshake headers
    wsClient = new ws(`ws://${ws_host}:${ws_port}`, {
        headers: {
            'password': password // Send password in handshake
        }
    });

    wsClient.on('open', () => {
        console.log('Connected to plugin');
    });

    wsClient.on('message', (data) => {
        // Handle incoming messages
        console.log('Received message:', data);
    });

    wsClient.on('error', (err) => {
        console.error('Error connecting to plugin:', err);
        reconnect(reconnectAttempts); // Reconnect on error
    });

    wsClient.on('close', (code, reason) => {
        console.warn(`Connection closed. Code: ${code}, Reason: ${reason}`);
        reconnect(reconnectAttempts); // Attempt reconnect on disconnect
    });
}

function sendMessage(message: string) {
    if (wsClient && wsClient.readyState === wsClient.OPEN) {
        wsClient.send(JSON.stringify(message));
    } else {
        console.warn('WebSocket is not open. Message not sent.');
        return;
    }
}

async function main() {
    try {
        setupWebSocket();

        const controller = new ArtNetController();
        controller.bind(process.env.ARTNET_BIND_IP);

        controller.on('dmx', (dmx) => {
            const uni = dmx.universe;
            const data = dmx.data;

            let channels = [];

            for (let i = 0; i < data.length; i++) {
                channels.push({
                    id: i,
                    value: data[i]
                });
            }

            let universe = {
                id: uni,
                channels: channels
            }

            sendMessage(JSON.stringify({
                type: 'dmx',
                data: [universe]
            }));
        });

    } catch (error) {
        console.error('Error during main execution:', error);
        process.exit(1);
    }
}

async function load() {
    try {
        await checkConfig();
        await main();
    } catch (error) {
        console.error('Failed to load application:', error);
        process.exit(1);
    }
}

load();

