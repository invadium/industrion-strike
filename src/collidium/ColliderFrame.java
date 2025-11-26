package collidium;

import javax.swing.JFrame;

public class ColliderFrame {

    public static void main(String[] args) {
        JFrame frame = new JFrame();

        frame.add(new Canvas3D());
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}
