package com.example.order_management.controller;

import com.example.order_management.dto.OrderParamsDTO;
import com.example.order_management.dto.OrderRequestDTO;
import com.example.order_management.dto.OrderResponseDTO;
import com.example.order_management.dto.UpdateOrderStatusDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;
import java.util.UUID;

@Tag(name = "Pedidos")
@RequestMapping("/orders")
public interface OrderController {
    
    @Operation(
            summary = "Criar pedidos",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pedido criado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponseDTO.class)))
            }
    )
    @PostMapping
    ResponseEntity<OrderResponseDTO> createOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do pedido a ser criado",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderRequestDTO.class))
            )
            @Valid @RequestBody OrderRequestDTO dto
    );
    
    @Operation(
            summary = "Listar pedidos com filtros e paginação",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista paginada de pedidos")
            }
    )
    @GetMapping
    ResponseEntity<Page<OrderResponseDTO>> listOrders(
            @ParameterObject @ModelAttribute OrderParamsDTO orderParams,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    );
    
    @Operation(
            summary = "Obter último relatório diário",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de pedidos no último relatório diário")
            }
    )
    @GetMapping("/report")
    ResponseEntity<List<OrderResponseDTO>> getLastDailyReport();
    
    @Operation(
            summary = "Atualizar status do pedido",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso")
            }
    )
    @PatchMapping("/{id}/status")
    ResponseEntity<OrderResponseDTO> updateStatus(
            @Parameter(description = "ID do pedido", required = true) @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Novo status do pedido",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateOrderStatusDTO.class))
            )
            @Valid @RequestBody UpdateOrderStatusDTO dto
    );
    
    @Operation(
            summary = "Cancelar pedido",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Pedido cancelado com sucesso")
            }
    )
    @DeleteMapping("/{id}")
    ResponseEntity<Void> cancelOrder(
            @Parameter(description = "ID do pedido", required = true) @PathVariable UUID id
    );
}