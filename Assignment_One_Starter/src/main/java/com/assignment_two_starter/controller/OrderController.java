package com.assignment_two_starter.controller;

import com.assignment_two_starter.dto.OrderSummaryDTO;
import com.assignment_two_starter.service.OrderService;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/api/orders")
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
            @RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String acceptHeader) {

            List<OrderSummaryDTO> ordersSummary = orderService.getAllOrders();

            if (ordersSummary.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No orders found.");
            }

            return formatResponse(acceptHeader, ordersSummary);
        }


    private ResponseEntity<?> formatResponse(String acceptHeader, List<OrderSummaryDTO> ordersSummary) {
        try {
            if ("application/x-yaml".equalsIgnoreCase(acceptHeader)) {
                String yamlResponse = new YAMLMapper().writeValueAsString(ordersSummary);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/x-yaml")).body(yamlResponse);
            }
            else if ("text/tab-separated-values".equalsIgnoreCase(acceptHeader)) {
                String tsvResponse = orderService.convertToallTSV(ordersSummary);
                return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/tab-separated-values")).body(tsvResponse);
            }
            else if (acceptHeader.contains(MediaType.APPLICATION_XML_VALUE)) {
                String xmlResponse = new XmlMapper().writeValueAsString(ordersSummary);
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xmlResponse);
            }
            else {
                return ResponseEntity.ok(ordersSummary);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing response.");
        }
    }




    /**
     * Fetch a specific order by ID (supports JSON, XML, YAML, and TSV).
     */
    @GetMapping(value = "/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/x-yaml", "text/tab-separated-values"})
    public ResponseEntity<?> getOrderById(
            @PathVariable Integer orderId,
            @RequestHeader(value = "Accept", defaultValue = MediaType.APPLICATION_JSON_VALUE) String acceptHeader) {

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
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Integer orderId) throws IOException {
        byte[] pdf = orderService.generateInvoice(orderId);
        if (pdf == null) {
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
