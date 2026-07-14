package com.example.formmanagement.validation;

import com.example.formmanagement.entity.Field;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

@Component
public class FieldValidatorEngine {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6})$");

    public void validate(Field field, String value) {
        if (Boolean.TRUE.equals(field.getRequired())) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Trường '" + field.getLabel() + "' là bắt buộc nhập.");
            }
        }

        if (value == null || value.trim().isEmpty()) {
            return;
        }

        switch (field.getType()) {
            case TEXT:
                if (value.length() > 200) {
                    throw new IllegalArgumentException("Trường '" + field.getLabel() + "' không được vượt quá 200 ký tự.");
                }
                break;
                
            case NUMBER:
                try {
                    double num = Double.parseDouble(value);
                    if (num < 0 || num > 100) {
                        throw new IllegalArgumentException("Trường '" + field.getLabel() + "' phải nằm trong khoảng từ 0 đến 100.");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Trường '" + field.getLabel() + "' phải là một số hợp lệ.");
                }
                break;
                
            case DATE:
                try {
                    LocalDate date = LocalDate.parse(value);
                    if (date.isBefore(LocalDate.now())) {
                        throw new IllegalArgumentException("Trường '" + field.getLabel() + "' không được phép chọn ngày trong quá khứ.");
                    }
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Trường '" + field.getLabel() + "' phải là định dạng ngày hợp lệ (YYYY-MM-DD).");
                }
                break;
                
            case COLOR:
                if (!HEX_COLOR_PATTERN.matcher(value).matches()) {
                    throw new IllegalArgumentException("Trường '" + field.getLabel() + "' phải là mã HEX hợp lệ (VD: #FF0000).");
                }
                break;
                
            case SELECT:
                if (field.getOptions() == null || !field.getOptions().contains(value)) {
                    throw new IllegalArgumentException("Giá trị đã chọn cho trường '" + field.getLabel() + "' không nằm trong danh sách hợp lệ.");
                }
                break;
                
            default:
                throw new IllegalArgumentException("Loại trường dữ liệu không được hỗ trợ.");
        }
    }
}
