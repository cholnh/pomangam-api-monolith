package kr.nzzi.msa.pmg.pomangamapimonilith.global.util.validation;


import kr.nzzi.msa.pmg.pomangamapimonilith.global.util.validation.annotation.Phone;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    @Override
    public void initialize(Phone phone) {

    }

    @Override
    public boolean isValid(String field, ConstraintValidatorContext cxt) {
        return field != null && field.matches("[0-9]+")
                && (field.length() > 8) && (field.length() < 14);
    }
}