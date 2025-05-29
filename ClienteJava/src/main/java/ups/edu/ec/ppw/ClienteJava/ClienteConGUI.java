package ups.edu.ec.ppw.ClienteJava;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;

public class ClienteConGUI extends JFrame {
    private JTextArea areaMensajes;
    private JTextField campoMensaje;
    private JButton botonEnviar;
    private WebSocketClient cliente;
    private String nombreUsuario;

    public ClienteConGUI() {
        // Solicita el nombre de usuario al iniciar
        nombreUsuario = JOptionPane.showInputDialog(this, "Ingresa tu nombre:", "Bienvenido", JOptionPane.PLAIN_MESSAGE);
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes ingresar . Cerrando aplicación.");
            System.exit(0);
        }

        setTitle("Chat WebSocket - " + nombreUsuario);
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        areaMensajes = new JTextArea();
        areaMensajes.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaMensajes);

        campoMensaje = new JTextField();
        botonEnviar = new JButton("Enviar");

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(campoMensaje, BorderLayout.CENTER);
        panelInferior.add(botonEnviar, BorderLayout.EAST);

        add(scroll, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        botonEnviar.addActionListener(this::enviarMensaje);

        conectarWebSocket();
    }

    private void conectarWebSocket() {
        try {
            cliente = new WebSocketClient(new URI("ws://172.16.215.31:8765")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    SwingUtilities.invokeLater(() -> areaMensajes.append("✅ Conectado al servidor\n"));
                }

                @Override
                public void onMessage(String message) {
                    SwingUtilities.invokeLater(() -> areaMensajes.append(message + "\n"));
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    SwingUtilities.invokeLater(() -> areaMensajes.append("❌ Conexión cerrada: " + reason + "\n"));
                }

                @Override
                public void onError(Exception ex) {
                    SwingUtilities.invokeLater(() -> areaMensajes.append("⚠️ Error: " + ex.getMessage() + "\n"));
                }
            };
            cliente.connect();
        } catch (Exception e) {
            areaMensajes.append("❌ No se pudo conectar: " + e.getMessage() + "\n");
        }
    }

    private void enviarMensaje(ActionEvent e) {
        String mensaje = campoMensaje.getText();
        if (!mensaje.isEmpty() && cliente != null && cliente.isOpen()) {
            String mensajeConNombre = nombreUsuario + ": " + mensaje;
            cliente.send(mensajeConNombre);
            areaMensajes.append("Tú: " + mensaje + "\n");
            campoMensaje.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClienteConGUI gui = new ClienteConGUI();
            gui.setVisible(true);
        });
    }
}
