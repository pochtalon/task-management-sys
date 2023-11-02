package mate.intro.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.intro.dto.label.CreateLabelRequestDto;
import mate.intro.dto.label.LabelDto;
import mate.intro.dto.label.UpdateLabelRequestDto;
import mate.intro.service.LabelService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Label management", description = "Endpoints for managing labels")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/labels")
public class LabelController {
    private final LabelService labelService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping()
    @Operation(summary = "Create new label", description = "Create new label")
    public LabelDto addLabel(@RequestBody @Valid CreateLabelRequestDto requestDto) {
        return labelService.save(requestDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    @Operation(summary = "Get labels", description = "Get all labels from db")
    public List<LabelDto> getLabels() {
        return labelService.getAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update label by id", description = "Update label by id")
    public LabelDto updateLabel(@PathVariable Long id,
                                 @RequestBody UpdateLabelRequestDto updateRequest) {
        return labelService.updateLabel(id, updateRequest);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete label by id", description = "Delete label by id")
    public void deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
    }
}
