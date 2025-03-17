package com.assignment_two_starter.service;

import com.assignment_two_starter.dto.OrderSummaryDTO;
import com.assignment_two_starter.model.OrderItems;
import com.assignment_two_starter.model.Orders;
import com.assignment_two_starter.repository.OrderRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // fetch all order
    public Page<OrderSummaryDTO> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Orders> ordersPage = orderRepository.findAll(pageable);
        return ordersPage.map(OrderSummaryDTO::new);
    }
    //get all tsv
    public String convertToallTSV(List<OrderSummaryDTO> ordersSummary) {
        StringBuilder builder = new StringBuilder();

        builder.append("Order ID\tCustomer Name\tTotal Amount\tStatus\tPayment Status\n");


        for (OrderSummaryDTO order : ordersSummary) {
            builder.append(order.getOrderId()).append("\t")
                    .append(order.getCustomerName()).append("\t")
                    .append(order.getTotalAmount()).append("\t")
                    .append(order.getStatus()).append("\t")
                    .append(order.getPaymentStatus()).append("\n");
        }

        return builder.toString();
    }


    public Optional<OrderSummaryDTO> getOrderSummary(Integer orderId) {
        return orderRepository.findById(orderId).map(OrderSummaryDTO::new);
    }

    //for id tsv
    public String convertToTSV(OrderSummaryDTO order) {
        StringBuilder tsv = new StringBuilder("Order ID\tCustomer Name\tTotal Amount\tStatus\tPayment Status\n");
        tsv.append(order.getOrderId()).append("\t")
                .append(order.getCustomerName()).append("\t")
                .append(order.getTotalAmount()).append("\t")
                .append(order.getStatus()).append("\t")
                .append(order.getPaymentStatus()).append("\n");

        return tsv.toString();
    }

    public byte[] generateInvoice(Integer orderId) {
        Orders order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return new byte[0];
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        document.add(new Paragraph("BabyNest Invoice")
                .setBold()
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Order ID: " + order.getOrderId()));
        document.add(new Paragraph("Customer: " + order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName()));
        document.add(new Paragraph("Shipping Address: " + (order.getShippingAddressId() != null ? order.getShippingAddressId().getStreetAddress() : "No Address Provided")));
        document.add(new Paragraph("Order Status: " + order.getStatus()));
        document.add(new Paragraph("Total Amount: $" + String.format("%.2f", order.getTotalAmount())));

        String paymentStatus = order.getPaymentsList().isEmpty() ? "Pending" : order.getPaymentsList().get(0).getStatus();
        document.add(new Paragraph("Payment Status: " + paymentStatus));

        document.add(new Paragraph("\n"));

        Table table = new Table(new float[]{3f, 1f, 1f, 1f});
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(new Cell().add(new Paragraph("Product").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Price").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Total").setBold()));

        boolean hasUnpaidItems = false;

        for (OrderItems item : order.getOrderItems()) {
            String itemPaymentStatus = item.getPaymentStatus();

            if ("PENDING".equalsIgnoreCase(itemPaymentStatus) || "CANCELLED".equalsIgnoreCase(itemPaymentStatus)) {
                hasUnpaidItems = true;
                table.addCell(new Cell().add(new Paragraph(item.getProduct().getName())));
                table.addCell(new Cell().add(new Paragraph("$" + String.format("%.2f", item.getUnitPrice()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
                table.addCell(new Cell().add(new Paragraph("$" + String.format("%.2f", item.getTotalPrice()))));
            }
        }

        if (!hasUnpaidItems) {
            document.add(new Paragraph("\nNo unpaid items found.").setBold().setTextAlignment(TextAlignment.CENTER));
        } else {
            document.add(table);
        }

        document.close();
        pdfDocument.close();

        return outputStream.toByteArray();
    }


}
