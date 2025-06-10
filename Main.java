import modules.Item;
import services.*;
import utils.InputUtil;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // === Init Services ===
        StoreService storeService = new StoreService();
        SalesTrackerService salesTracker = new SalesTrackerService();
        DaySessionService daySession = new DaySessionService(salesTracker);
        ReportService reportService = new ReportService(salesTracker, daySession);

        // === Load Items ===
        storeService.loadItems();
        List<Item> items = storeService.getAllItems();

        boolean running = true;

        while (running) {
            System.out.println("\n🛒 Grocery Billing System");
            System.out.println("==============================");
            System.out.println("1. Begin Day");
            System.out.println("2. Create Bill");
            System.out.println("3. End Day & Print Summary");
            System.out.println("4. Exit");
            System.out.println("==============================");

            int choice = InputUtil.readInt("Enter choice: ");
            switch (choice) {
                case 1:
                    daySession.beginDay();
                    break;

                case 2:
                    if (!daySession.isDayStarted()) {
                        System.out.println("❌ Please begin the day first.");
                        break;
                    }

                    CartService cartService = new CartService();
                    boolean billing = true;

                    while (billing) {
                        System.out.println("\nAvailable Items:");
                        for (Item item : items) {
                            System.out.printf("%d. %s - ₹%.2f\n", item.getId(), item.getName(), item.getPrice());
                        }

                        int itemId = InputUtil.readInt("Enter item ID to add (0 to finish): ");
                        if (itemId == 0) break;

                        Item selected = storeService.getItemById(itemId);
                        if (selected == null) {
                            System.out.println("❌ Invalid item ID.");
                            continue;
                        }

                        int qty = InputUtil.readInt("Enter quantity: ");
                        cartService.addItem(selected, qty);
                    }

                    int billNo = daySession.getNextBillSerial();
                    cartService.printCart(billNo);
                    salesTracker.recordSale(cartService.getCartItems());
                    cartService.clearCart();

                    InputUtil.pressEnterToContinue();
                    break;

                case 3:
                    if (!daySession.isDayStarted()) {
                        System.out.println("❌ No active day to end.");
                        break;
                    }

                    daySession.endDay();
                    reportService.printSummary();
                    reportService.generatePDF();
                    InputUtil.pressEnterToContinue();
                    break;

                case 4:
                    running = false;
                    System.out.println("👋 Exiting... Goodbye!");
                    break;

                default:
                    System.out.println("❌ Invalid choice.");
            }
        }
    }
}
