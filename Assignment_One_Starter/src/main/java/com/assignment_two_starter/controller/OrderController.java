package com.assignment_two_starter.controller;

import com.assignment_two_starter.dto.OrderSummaryDTO;
import com.assignment_two_starter.service.OrderService;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/api/orders")
@SecurityRequirement(name = "BearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Fetch all order  (supports JSON, XML, YAML, and TSV).
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/x-yaml", "text/tab-separated-values"})
    public ResponseEntity<?> getAllOrders(
            @RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String acceptHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admins only.");
        }

        Page<OrderSummaryDTO> ordersPage = orderService.getAllOrders(page, size);

        if (ordersPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No orders found.");
        }

        return formatResponse(acceptHeader, (Page<OrderSummaryDTO>) ordersPage);
    }


    private ResponseEntity<?> formatResponse(String acceptHeader, Page<OrderSummaryDTO> ordersPage) {
        try {
            if ("application/x-yaml".equalsIgnoreCase(acceptHeader)) {
                YAMLMapper yamlMapper = new YAMLMapper();
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/x-yaml"))
                        .body(yamlMapper.writeValueAsString(ordersPage));
            }
            else if ("text/tab-separated-values".equalsIgnoreCase(acceptHeader)) {
                StringBuilder tsvResponse = new StringBuilder("Order ID\tCustomer Name\tTotal Amount\tStatus\tPayment Status\n");
                for (OrderSummaryDTO order : ordersPage.getContent()) {
                    tsvResponse.append(order.getOrderId()).append("\t")
                            .append(order.getCustomerName()).append("\t")
                            .append(order.getTotalAmount()).append("\t")
                            .append(order.getStatus()).append("\t")
                            .append(order.getPaymentStatus()).append("\n");
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("text/tab-separated-values"))
                        .body(tsvResponse.toString());
            }
            else if (acceptHeader.contains(MediaType.APPLICATION_XML_VALUE)) {
                XmlMapper xmlMapper = new XmlMapper();
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_XML)
                        .body(xmlMapper.writeValueAsString(ordersPage));
            }
            else {
                return ResponseEntity.ok(ordersPage);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request.");
        }
    }





    /**
     * Fetch a specific order by ID (supports JSON, XML, YAML, and TSV).
     */
    @GetMapping(value = "/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/x-yaml", "text/tab-separated-values"})
    public ResponseEntity<?> getOrderById(
            @PathVariable Integer orderId,
            @RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String acceptHeader, Authentication authentication) {


        if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Admins only.");
        }

        Optional<OrderSummaryDTO> orderSummaryOptional = orderService.getOrderSummary(orderId);
        if (orderSummaryOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        }

        OrderSummaryDTO orderSummary = orderSummaryOptional.get();
        return formatResponse(acceptHeader, orderSummary);
    }

    /**
     * Generate a PDF invoice for a specific order.
     */
    @GetMapping(value = "/{orderId}/invoice", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Integer orderId, Authentication authentication) throws IOException {

        if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: Admins only.".getBytes());
        }

        byte[] pdf = orderService.generateInvoice(orderId);
        if (pdf == null || pdf.length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("invoice_" + orderId + ".pdf").build());

        return ResponseEntity.ok().headers(headers).body(pdf);
    }


    /**
     * Converts response based on the Accept header.
     */
    private ResponseEntity<?> formatResponse(String acceptHeader, OrderSummaryDTO orderSummary) {
        try {
            if ("application/x-yaml".equalsIgnoreCase(acceptHeader)) {
                YAMLMapper yamlMapper = new YAMLMapper();
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/x-yaml"))
                        .body(yamlMapper.writeValueAsString(orderSummary));
            }
            if ("text/tab-separated-values".equalsIgnoreCase(acceptHeader)) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("text/tab-separated-values"))
                        .body(orderService.convertToTSV(orderSummary));
            }
            if ("application/xml".equalsIgnoreCase(acceptHeader)) {
                XmlMapper xmlMapper = new XmlMapper();
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_XML)
                        .body(xmlMapper.writeValueAsString(orderSummary));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request.");
        }

        return ResponseEntity.ok(orderSummary);
    }

}
