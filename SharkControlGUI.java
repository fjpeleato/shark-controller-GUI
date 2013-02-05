import gnu.io.*;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.*;

/**
 * GUI to control the shark in the Air Aquarium project. Communicates via serial port.
 * @author Francisco Peleato
 */
public class SharkControlGUI extends javax.swing.JFrame {

    /**
     * Instance variables
     */
    
    // Buttons
    private javax.swing.JButton upButton;
    private javax.swing.JButton downButton;
    private javax.swing.JButton rightButton;
    private javax.swing.JButton leftButton;
    
    // Labels
    private javax.swing.JLabel forwardLabel;
    private javax.swing.JLabel heightLabel;
    private javax.swing.JLabel throttleLabel;
    private javax.swing.JLabel fullLabel;
    private javax.swing.JLabel highLabel;
    private javax.swing.JLabel mediumLabel;
    private javax.swing.JLabel slowLabel;
    private javax.swing.JLabel stoptLabel;
    
    // TextFields
    private static javax.swing.JTextField forwardTextField;
    private static javax.swing.JTextField heightTextField;
    
    // Other swing elements
    private javax.swing.JCheckBox autoCheckBox;	// CheckBox for AutoShark
    private javax.swing.JSlider throttleSlider; // Slider for Throttle
    
    // Other instance variables
    private static boolean continueThread;      // Flag for thread interruption
    private static InputStream in;              // In stream
    private static OutputStream out;            // Out stream
   
    /**
     * Constants
     */
    
    // Forward throttle messages
    private final static String THROTTLE_0 = "{1|6|0|01|0}";
    private final static String THROTTLE_25 = "{1|6|0|01|1}";
    private final static String THROTTLE_50 = "{1|6|0|01|2}";
    private final static String THROTTLE_75 = "{1|6|0|01|3}";
    private final static String THROTTLE_100 = "{1|6|0|01|4}";
    
    // Autopilot messages
    private final static String AUTO_OFF = "{1|7|0|01|0}";
    private final static String AUTO_ON = "{1|7|0|01|1}";
    
    // Direction messages
    private final static String UP_OFF = "{1|2|0|01|0}";
    private final static String UP_ON = "{1|2|0|01|1}";
    private final static String DOWN_OFF = "{1|3|0|01|0}";
    private final static String DOWN_ON = "{1|3|0|01|1}";
    private final static String RIGHT_OFF = "{1|4|0|01|0}";
    private final static String RIGHT_ON = "{1|4|0|01|1}";
    private final static String LEFT_OFF = "{1|5|0|01|0}";
    private final static String LEFT_ON = "{1|5|0|01|1}";
    
    // Sensor reading request messages
    private final static String HEIGHT_SENSOR_REQUEST = "{0|0|0|00}";
    private final static String FORWARD_SENSOR_REQUEST = "{0|1|0|00}";
    
    // Other constants
    private final static int LEFT_CURLY_BRACE_ASCII = 123;
    private final static int RIGHT_CURLY_BRACE_ASCII = 125;
    private final static String MESSAGE_SEPARATOR = "|";
    private final static int HEIGHT_SENSOR_INDEX = 0;
    private final static int FORWARD_SENSOR_INDEX = 1;

    /**
     * Constructor. Creates new form SharkControlGUI
     */
    public SharkControlGUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        throttleSlider = new javax.swing.JSlider();
        fullLabel = new javax.swing.JLabel();
        highLabel = new javax.swing.JLabel();
        mediumLabel = new javax.swing.JLabel();
        slowLabel = new javax.swing.JLabel();
        stoptLabel = new javax.swing.JLabel();
        throttleLabel = new javax.swing.JLabel();
        autoCheckBox = new javax.swing.JCheckBox();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        rightButton = new javax.swing.JButton();
        leftButton = new javax.swing.JButton();
        forwardLabel = new javax.swing.JLabel();
        heightLabel = new javax.swing.JLabel();
        forwardTextField = new javax.swing.JTextField();
        heightTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("SharkControl v1.0");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setName("frame"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        throttleSlider.setMinorTickSpacing(25);
        throttleSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        throttleSlider.setPaintTicks(true);
        throttleSlider.setSnapToTicks(true);
        throttleSlider.setValue(0);
        throttleSlider.addChangeListener(new javax.swing.event.ChangeListener() {

            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                throttleSliderStateChanged(evt);
            }
        });

        /**
         * Labels
         */
        
        // Trottle slider
        throttleLabel.setFont(new java.awt.Font("Tahoma", 1, 12));
        throttleLabel.setText("Forward throttle");
        fullLabel.setText("Full");
        highLabel.setText("High");
        mediumLabel.setText("Medium");
        slowLabel.setText("Slow");
        stoptLabel.setText("Stop");

        // Autopilot checkbox
        autoCheckBox.setFont(new java.awt.Font("Tahoma", 1, 12));
        autoCheckBox.setText("AutoShark");
        autoCheckBox.addItemListener(new java.awt.event.ItemListener() {

            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoCheckBoxItemStateChanged(evt);
            }
        });

        /* Direction buttons */
        // Up button
        upButton.setText("Up");
        upButton.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                upButtonMouseReleased(evt);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                upButtonMousePressed(evt);
            }
        });

        // Down button
        downButton.setText("Down");
        downButton.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                downButtonMouseReleased(evt);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                downButtonMousePressed(evt);
            }
        });

        // Right button
        rightButton.setText("Right");
        rightButton.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                rightButtonMouseReleased(evt);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                rightButtonMousePressed(evt);
            }
        });

        // Left button
        leftButton.setText("Left");
        leftButton.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                leftButtonMouseReleased(evt);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                leftButtonMousePressed(evt);
            }
        });

        /* Sensor readings displays */
        // Forward sensor
        forwardLabel.setText("Forward sensor");
        forwardTextField.setBackground(new java.awt.Color(0, 0, 0));
        forwardTextField.setEditable(false);
        forwardTextField.setFont(new java.awt.Font("Lucida Console", 1, 12));
        forwardTextField.setForeground(new java.awt.Color(51, 204, 0));
        forwardTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        forwardTextField.setToolTipText("");

        // Height sensor
        heightLabel.setText("Height sensor");
        heightTextField.setBackground(new java.awt.Color(0, 0, 0));
        heightTextField.setEditable(false);
        heightTextField.setFont(new java.awt.Font("Lucida Console", 1, 12));
        heightTextField.setForeground(new java.awt.Color(51, 204, 0));
        heightTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        heightTextField.setToolTipText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(forwardLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(forwardTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addComponent(heightLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(heightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(13, 13, 13).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(downButton).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(67, 67, 67).addComponent(upButton)).addComponent(leftButton))).addGap(3, 3, 3).addComponent(rightButton, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(layout.createSequentialGroup().addGap(22, 22, 22).addComponent(autoCheckBox))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(throttleLabel).addGroup(layout.createSequentialGroup().addComponent(throttleSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(stoptLabel).addComponent(slowLabel).addComponent(mediumLabel).addComponent(highLabel).addComponent(fullLabel)))))).addContainerGap()));

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[]{downButton, leftButton, rightButton, upButton});

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(forwardLabel).addComponent(forwardTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(heightLabel).addComponent(heightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(32, 32, 32).addComponent(upButton, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(leftButton).addComponent(rightButton)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(downButton).addGap(26, 26, 26).addComponent(autoCheckBox)).addGroup(layout.createSequentialGroup().addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(throttleLabel).addGap(6, 6, 6).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addGroup(layout.createSequentialGroup().addComponent(fullLabel).addGap(29, 29, 29).addComponent(highLabel).addGap(28, 28, 28).addComponent(mediumLabel).addGap(29, 29, 29).addComponent(slowLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(stoptLabel)).addComponent(throttleSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))).addContainerGap(15, Short.MAX_VALUE)));

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[]{downButton, leftButton, rightButton, upButton});

        pack();
    }

    /**
     * Event listeners
     */
    
    // Forward throttle
    private void throttleSliderStateChanged(javax.swing.event.ChangeEvent evt) {

        int value = throttleSlider.getValue();

        // If throttle set to 0%
        if (value == 0) {
            System.out.println("Forward Throttle set to: Stop");
            sendPacket(THROTTLE_0);
        } 
        // If throttle set to 25%
        else if (value == 25) {
            System.out.println("Forward Throttle set to: Slow");
            sendPacket(THROTTLE_25);
        } 
        // If throttle set to 50%
        else if (value == 50) {
            System.out.println("Forward Throttle set to: Medium");
            sendPacket(THROTTLE_50);
        } 
        // If throttle set to 75%
        else if (value == 75) {
            System.out.println("Forward Throttle set to: High");
            sendPacket(THROTTLE_75);
        } 
        // If throttle set to 100%
        else if (value == 100) {
            System.out.println("Forward Throttle set to: Full");
            sendPacket(THROTTLE_100);
        } 
        // If throttle set to values in-between, do nothing
        else {
        }
    }

    // Autopilot checkbox
    private void autoCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {

        // If the checkbox is selected
        if (autoCheckBox.isSelected()) {
            // Disable the manual controls
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            rightButton.setEnabled(false);
            leftButton.setEnabled(false);

            System.out.println("AutoShark: On");
            sendPacket(AUTO_ON);
        } 
        // If the checkbox is deselected
        else {
            // Enable the manual controls
            upButton.setEnabled(true);
            downButton.setEnabled(true);
            rightButton.setEnabled(true);
            leftButton.setEnabled(true);

            System.out.println("AutoShark: Off");
            sendPacket(AUTO_OFF);
        }
    }

    // Up button pressed
    private void upButtonMousePressed(java.awt.event.MouseEvent evt) {

        // Works only if the autoCheckBox is deselected
        if (!autoCheckBox.isSelected()) {
            System.out.println("Up selected");
            sendPacket(UP_ON);
        } else {
        }
    }

    // Up button released
    private void upButtonMouseReleased(java.awt.event.MouseEvent evt) {

        // Works only if the autoCheckBox is deselected
        if (!autoCheckBox.isSelected()) {
            System.out.println("Up deselected");
            sendPacket(UP_OFF);
        } else {
        }
    }

    // Down button pressed
    private void downButtonMousePressed(java.awt.event.MouseEvent evt) {

        // Works only if the autoCheckBox is deselected
        if (!autoCheckBox.isSelected()) {
            System.out.println("Down selected");
            sendPacket(DOWN_ON);
        } else {
        }
    }

    // Down button released
    private void downButtonMouseReleased(java.awt.event.MouseEvent evt) {

        // Works only if the autoCheckBox is deselected
        if (!autoCheckBox.isSelected()) {
            System.out.println("Down deselected");
            sendPacket(DOWN_OFF);
        } else {
        }
    }

    // Right button pressed
    private void rightButtonMousePressed(java.awt.event.MouseEvent evt) {

        // Works only if the autoCheckBox is deselected
        if (!autoCheckBox.isSelected()) {
            System.out.println("Right selected");
            sendPacket(RIGHT_ON);
        } else {
        }
    }

    // Right button released
    private void rightButtonMouseReleased(java.awt.event.MouseEvent evt) {

        // Works only if the autoCheckBox is deselected
        if (!autoCheckBox.isSelected()) {
            System.out.println("Right deselected");
            sendPacket(RIGHT_OFF);
        } else {
        }
    }

    // Left button pressed
    private void leftButtonMousePressed(java.awt.event.MouseEvent evt) {

        // Works only if the autoCheckBox is deselected
        if (!autoCheckBox.isSelected()) {
            System.out.println("Left selected");
            sendPacket(LEFT_ON);
        } else {
        }
    }

    // Left button released
    private void leftButtonMouseReleased(java.awt.event.MouseEvent evt) {

        // Works only if the autoCheckBox is deselected
        if (!autoCheckBox.isSelected()) {
            System.out.println("Left deselected");
            sendPacket(LEFT_OFF);
        } else {
        }
    }

    // Window closed
    private void formWindowClosing(java.awt.event.WindowEvent evt) {

        // If AutoShark is active, turn it off before closing
        if (autoCheckBox.isSelected()) {
            System.out.println("AutoShark: Off");
            sendPacket(AUTO_OFF);
        }
        // Terminate all threads
        continueThread = false;
        System.exit(0);
    }

    /**
     * Serial communication
     */
    
    // Lists available serial ports. Returns chosen port
    private static String selectPort() {

        // To contain the list of available ports
        ArrayList<String> ports = new ArrayList<String>();

        // Get ports and add them to the list
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portId = portEnum.nextElement();
            ports.add(portId.getName());
        }

        // Show selection window and return the seletion
        return ((String) JOptionPane.showInputDialog(null, "Choose a port",
                "SharkControl v1.0", JOptionPane.QUESTION_MESSAGE, null,
                ports.toArray(), null));
    }

    // Class for RXTX serial communication
    private static class SerialComm {

        SerialComm() {
            super();
        }

        void connect(String portName) throws Exception {
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
            if (portId.isCurrentlyOwned()) {
                System.out.println("Error: Port is currently in use");
            } else {
                CommPort commPort = portId.open(this.getClass().getName(),
                        5000);

                if (commPort instanceof SerialPort) {
                    SerialPort serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                    in = serialPort.getInputStream();
                    out = serialPort.getOutputStream();

                    new Thread(new SerialReader(in)).start();
                    new Thread(new SerialWriter(out)).start();
                } else {
                    System.out.println("Error: Only serial ports are handled");
                }
            }
        }

        static class SerialReader implements Runnable {

            InputStream in;

            SerialReader(InputStream in) {
                this.in = in;
            }

            public void run() {
                byte[] buffer = new byte[1024];
                int len = -1;
                try {
                    while ((len = this.in.read(buffer)) > -1) {
                        System.out.println(new String(buffer, 0, len));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        static class SerialWriter implements Runnable {

            OutputStream out;

            SerialWriter(OutputStream out) {
                this.out = out;
            }

            public void run() {
                try {
                    int c = 0;
                    while ((c = System.in.read()) > -1) {
                        this.out.write(c);
                        System.out.println(c);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Sends a packet over the serial port
    private static void sendPacket(String packet) {
        try {
            out.write(packet.getBytes());
            System.out.println("Packet sent: " + packet);
        } catch (IOException e) {
            System.out.println("Error: Packet not sent: " + packet);
        }
    }

    // Requests the sensors' readings
    private static class sensorRequestThread extends Thread {

        public void run() {
            try {
                while (continueThread) {
                    // Requests the readings for both sensors
                    sendPacket(FORWARD_SENSOR_REQUEST);
                    sendPacket(HEIGHT_SENSOR_REQUEST);

                    // Waits 1500 ms before requesting again
                    Thread.sleep(1500);
                }
            } catch (InterruptedException e) {
            }
        }
    }

    // Class to check if the sensors' readings are received
    private static class sensorReceiveThread extends Thread {

        public void run() {
            char[] buffer = new char[1024]; // To store the characters received
            boolean bufferFlag = true;      // To stop looping and allow the thread to sleep
            int bufferPointer = 0;          // Index of next available space
            int reading;                    // Reading obtained from the sensors

            while (continueThread) {
                while (bufferFlag) {
                    try {
                        // Obtain reading
                        reading = in.read();

                        // If the reading is empty (-1), stop looping
                        if (reading < 0) {
                            bufferFlag = false;
                        } 
                        // If reading is '{', start filling the buffer from 0
                        else if (reading == LEFT_CURLY_BRACE_ASCII) {
                            bufferPointer = 0;
                        } 
                        // If reading is '}', message is complete - update the sensors' displays
                        else if (reading == RIGHT_CURLY_BRACE_ASCII) {
                            updateDisplay(buffer);
                        } 
                        // Else, keep adding the characters to the buffer
                        else {
                            buffer[bufferPointer++] = (char) reading;
                        }
                    } catch (IOException e) {
                        System.out.println("IOException occurred while receiving a packet");
                    }
                    try {
                        Thread.sleep(1);
                        bufferFlag = true;
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    // Updates the sensors' displays with the value received
    private static void updateDisplay(char[] buffer) {
        String data = "";	// New value of display

        // Divide the buffered message into tokens
        StringTokenizer st = new StringTokenizer(buffer.toString(), MESSAGE_SEPARATOR);

        // Loop until last token is stored
        while (st.hasMoreTokens()) {
            data = st.nextToken();
        }

        // Update the corresponding display
        if (buffer[2] == HEIGHT_SENSOR_INDEX) {
            heightTextField.setText(data);
        }
        if (buffer[2] == FORWARD_SENSOR_INDEX) {
            forwardTextField.setText(data);
        }
    }
    
    /**
     * Main method
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    // Select port and start communication
                    new SerialComm().connect(selectPort());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                // Setup Nimbus skin
                try {
                    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                } catch (Exception e) {
                    // If Nimbus is not available, fall back to cross-platform
                    try {
                        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    } catch (Exception ex) {
                        // Reinstall Java
                    }
                }

                // Start sensor threads and GUI
                continueThread = true;
                new sensorRequestThread().start();
                new sensorReceiveThread().start();
                new SharkControlGUI().setVisible(true);
            }
        });
    }
}