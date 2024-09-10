import React, { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { toast } from "react-toastify";

const ProgressBar = ({ onComplete }) => {
    const [progress, setProgress] = useState(0);
    const [details, setDetails] = useState({ generated: 0, total: 0 });

    useEffect(() => {
        // Create a SockJS client
        const socket = new SockJS('/websocket');
        const stompClient = new Client({
            webSocketFactory: () => socket,
            debug: (str) => {
                console.log(str);
            },
            reconnectDelay: 5000, // Attempt to reconnect after 5 seconds if disconnected
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        // Connect to the WebSocket server
        stompClient.onConnect = () => {
            console.log('Connected to WebSocket');
            // Subscribe to the topic
            stompClient.subscribe('/topic/ndr-status', (message) => {
                const progressUpdate = JSON.parse(message.body);
                setProgress(progressUpdate.progress);
                setDetails({
                    generated: progressUpdate.generated,
                    total: progressUpdate.total
                });

                if (progressUpdate.progress >= 100) {
                    onComplete();
                }
            });
        };

        stompClient.onStompError = (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        };

        stompClient.onWebSocketClose = () => {
            console.log('WebSocket connection closed.');
        };

        stompClient.activate(); // Ensure client is activated

        // Cleanup on component unmount
        return () => {
            if (stompClient.active) {
                stompClient.deactivate(); // Deactivate the client when the component unmounts
            }
        };
    }, [onComplete]);

    return (
        <div>
            <h3>Processing Progress</h3>
            <div style={{ width: '100%', backgroundColor: '#e0e0df' }}>
                <div
                    style={{
                        width: `${progress}%`,
                        height: '24px',
                        backgroundColor: '#76c7c0',
                    }}
                ></div>
            </div>
            <p>{progress.toFixed(2)}% (Generated: {details.generated}/{details.total})</p>
        </div>
    );
};

export default ProgressBar;