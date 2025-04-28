package com.securevault;
import com.securevault.controller.LoginController;
import com.securevault.utils.DBConnection;
import javax.swing.SwingUtilities;
public class App {
public static void main(String[] args) {
// Initialize DB
DBConnection.getInstance().initDatabase();
// Launch Login UI on Event Dispatch Thread
SwingUtilities.invokeLater(() -> {
new LoginController();
});
}
}