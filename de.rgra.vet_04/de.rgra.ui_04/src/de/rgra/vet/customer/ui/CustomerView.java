package de.rgra.vet.customer.ui;

import java.util.List;
import java.util.ServiceLoader;

import de.rgra.vet.customer.model.Customer;
import de.rgra.vet.customer.nl.Messages;
import de.rgra.vet.customer.spi.CustomerViewTabService;
import de.rgra.vet.invoice.db.InvoiceDao;
import de.rgra.vet.invoice.model.Invoice;
import de.rgra.vet.invoice.ui.InvoiceTable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class CustomerView extends BorderPane {

	private final Customer customer;

	public CustomerView(Customer customer) {
		this.customer = customer;
		setTop(new CustomerInfoPane(customer));
		setCenter(createTabPane());
	}

	private Node createTabPane() {
		TabPane tabPane = new TabPane();

		ServiceLoader.load(CustomerViewTabService.class).stream()
				.flatMap(proxy -> proxy.get().createTabs(this, customer).stream())
				.forEach(tabPane.getTabs()::add);

		tabPane.getTabs()
			.add(createTab(Messages.getString("CustomerView.invoices"), createInvoiceNode()));
		return tabPane;
	}

	private Node createInvoiceNode() {
		List<Invoice> invoices = new InvoiceDao().fetchInvoicesForCustomer(customer.getId());
		InvoiceTable table = new InvoiceTable(invoices);
		return table;
	}

	public void replace(BorderPane newView) {
		BorderPane parent = (BorderPane) this.getParent();
		parent.setCenter(newView);
	}

	// ######
	private Tab createTab(String name, Node node) {
		Tab tab = new Tab();
		tab.setText(name);
		tab.setContent(node);
		return tab;
	}

}
