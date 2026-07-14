package com.example.formmanagement.validation;

import com.example.formmanagement.entity.Field;
import com.example.formmanagement.entity.FieldType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [Unit Test] Test thuật toán validate động của FieldValidatorEngine.
 * Không cần Spring Context — class này độc lập hoàn toàn.
 */
class FormValidationEngineTest {

    private FieldValidatorEngine validatorEngine;

    @BeforeEach
    void setUp() {
        validatorEngine = new FieldValidatorEngine();
    }

    // ================================================================
    // REQUIRED FIELD
    // ================================================================

    @Test
    @DisplayName("required=true, value rỗng → ném IllegalArgumentException")
    void validate_RequiredField_WithEmptyValue_ShouldThrow() {
        Field field = Field.builder()
                .id(1L).label("Họ tên").type(FieldType.TEXT).required(true).build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validatorEngine.validate(field, "")
        );

        assertTrue(ex.getMessage().contains("Họ tên"));
        assertTrue(ex.getMessage().contains("bắt buộc"));
    }

    @Test
    @DisplayName("required=true, value null → ném IllegalArgumentException")
    void validate_RequiredField_WithNullValue_ShouldThrow() {
        Field field = Field.builder()
                .id(1L).label("Email").type(FieldType.TEXT).required(true).build();

        assertThrows(IllegalArgumentException.class,
                () -> validatorEngine.validate(field, null));
    }

    @Test
    @DisplayName("required=false, value rỗng → không ném exception (bỏ qua validate)")
    void validate_OptionalField_WithEmptyValue_ShouldNotThrow() {
        Field field = Field.builder()
                .id(1L).label("Ghi chú").type(FieldType.TEXT).required(false).build();

        assertDoesNotThrow(() -> validatorEngine.validate(field, ""));
    }

    // ================================================================
    // TEXT
    // ================================================================

    @Test
    @DisplayName("TEXT - Nội dung hợp lệ (≤200 ký tự) → không ném exception")
    void validate_TextField_WithValidValue_ShouldNotThrow() {
        Field field = Field.builder()
                .id(1L).label("Họ tên").type(FieldType.TEXT).required(false).build();

        assertDoesNotThrow(() -> validatorEngine.validate(field, "Nguyen Van A"));
    }

    @Test
    @DisplayName("TEXT - Nội dung vượt 200 ký tự → ném IllegalArgumentException")
    void validate_TextField_WhenValueExceeds200Chars_ShouldThrow() {
        Field field = Field.builder()
                .id(1L).label("Mô tả").type(FieldType.TEXT).required(false).build();
        String longText = "a".repeat(201);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validatorEngine.validate(field, longText)
        );

        assertTrue(ex.getMessage().contains("200 ký tự"));
    }

    // ================================================================
    // NUMBER
    // ================================================================

    @Test
    @DisplayName("NUMBER - Số hợp lệ trong [0, 100] → không ném exception")
    void validate_NumberField_WithValidValue_ShouldNotThrow() {
        Field field = Field.builder()
                .id(1L).label("Điểm").type(FieldType.NUMBER).required(false).build();

        assertDoesNotThrow(() -> validatorEngine.validate(field, "85"));
        assertDoesNotThrow(() -> validatorEngine.validate(field, "0"));
        assertDoesNotThrow(() -> validatorEngine.validate(field, "100"));
    }

    @Test
    @DisplayName("NUMBER - Số âm → ném IllegalArgumentException")
    void validate_NumberField_WithNegativeValue_ShouldThrow() {
        Field field = Field.builder()
                .id(1L).label("Điểm").type(FieldType.NUMBER).required(false).build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validatorEngine.validate(field, "-1")
        );

        assertTrue(ex.getMessage().contains("0 đến 100"));
    }

    @Test
    @DisplayName("NUMBER - Số vượt 100 → ném IllegalArgumentException")
    void validate_NumberField_WithValueOver100_ShouldThrow() {
        Field field = Field.builder()
                .id(1L).label("Điểm").type(FieldType.NUMBER).required(false).build();

        assertThrows(IllegalArgumentException.class,
                () -> validatorEngine.validate(field, "101"));
    }

    @Test
    @DisplayName("NUMBER - Giá trị không phải số → ném IllegalArgumentException")
    void validate_NumberField_WithNonNumericValue_ShouldThrow() {
        Field field = Field.builder()
                .id(1L).label("Điểm").type(FieldType.NUMBER).required(false).build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validatorEngine.validate(field, "abc")
        );

        assertTrue(ex.getMessage().contains("số hợp lệ"));
    }

    // ================================================================
    // DATE
    // ================================================================

    @Test
    @DisplayName("DATE - Ngày tương lai hợp lệ → không ném exception")
    void validate_DateField_WithFutureDate_ShouldNotThrow() {
        Field field = Field.builder()
                .id(1L).label("Ngày họp").type(FieldType.DATE).required(false).build();
        String futureDate = LocalDate.now().plusDays(5).toString();

        assertDoesNotThrow(() -> validatorEngine.validate(field, futureDate));
    }

    @Test
    @DisplayName("DATE - Ngày trong quá khứ → ném IllegalArgumentException")
    void validate_DateField_WithPastDate_ShouldThrow() {
        Field field = Field.builder()
                .id(1L).label("Ngày họp").type(FieldType.DATE).required(false).build();
        String pastDate = LocalDate.now().minusDays(1).toString();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validatorEngine.validate(field, pastDate)
        );

        assertTrue(ex.getMessage().contains("quá khứ"));
    }

    @Test
    @DisplayName("DATE - Định dạng sai → ném IllegalArgumentException")
    void validate_DateField_WithInvalidFormat_ShouldThrow() {
        Field field = Field.builder()
                .id(1L).label("Ngày sinh").type(FieldType.DATE).required(false).build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validatorEngine.validate(field, "14-07-2026")
        );

        assertTrue(ex.getMessage().contains("YYYY-MM-DD"));
    }

    // ================================================================
    // COLOR
    // ================================================================

    @Test
    @DisplayName("COLOR - Mã HEX hợp lệ → không ném exception")
    void validate_ColorField_WithValidHex_ShouldNotThrow() {
        Field field = Field.builder()
                .id(1L).label("Màu nền").type(FieldType.COLOR).required(false).build();

        assertDoesNotThrow(() -> validatorEngine.validate(field, "#FF0000"));
        assertDoesNotThrow(() -> validatorEngine.validate(field, "#1a2b3c"));
    }

    @Test
    @DisplayName("COLOR - Mã HEX không hợp lệ → ném IllegalArgumentException")
    void validate_ColorField_WithInvalidHex_ShouldThrow() {
        Field field = Field.builder()
                .id(1L).label("Màu nền").type(FieldType.COLOR).required(false).build();

        assertThrows(IllegalArgumentException.class,
                () -> validatorEngine.validate(field, "red"));

        assertThrows(IllegalArgumentException.class,
                () -> validatorEngine.validate(field, "#GGG"));

        assertThrows(IllegalArgumentException.class,
                () -> validatorEngine.validate(field, "FF0000")); // thiếu dấu #
    }

    // ================================================================
    // SELECT
    // ================================================================

    @Test
    @DisplayName("SELECT - Giá trị nằm trong options → không ném exception")
    void validate_SelectField_WithValidOption_ShouldNotThrow() {
        Field field = Field.builder()
                .id(1L).label("Phòng ban").type(FieldType.SELECT).required(false)
                .options(List.of("IT", "HR", "Finance"))
                .build();

        assertDoesNotThrow(() -> validatorEngine.validate(field, "IT"));
    }

    @Test
    @DisplayName("SELECT - Giá trị không nằm trong options → ném IllegalArgumentException")
    void validate_SelectField_WithInvalidOption_ShouldThrow() {
        Field field = Field.builder()
                .id(1L).label("Phòng ban").type(FieldType.SELECT).required(false)
                .options(List.of("IT", "HR", "Finance"))
                .build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validatorEngine.validate(field, "Marketing")
        );

        assertTrue(ex.getMessage().contains("danh sách hợp lệ"));
    }

    @Test
    @DisplayName("SELECT - options null → ném IllegalArgumentException")
    void validate_SelectField_WithNullOptions_ShouldThrow() {
        Field field = Field.builder()
                .id(1L).label("Phòng ban").type(FieldType.SELECT).required(false)
                .options(null)
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> validatorEngine.validate(field, "IT"));
    }
}
