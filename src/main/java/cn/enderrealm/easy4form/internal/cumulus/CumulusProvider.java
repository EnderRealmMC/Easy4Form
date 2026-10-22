package cn.enderrealm.easy4form.internal.cumulus;

import cn.enderrealm.easy4form.internal.ButtonDefinition;
import cn.enderrealm.easy4form.internal.CustomComponent;
import cn.enderrealm.easy4form.internal.FormDefinition;
import cn.enderrealm.easy4form.internal.FormPlatform;
import cn.enderrealm.easy4form.internal.FormProvider;
import cn.enderrealm.easy4form.internal.FormResponse;
import cn.enderrealm.easy4form.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.DropdownComponent;
import org.geysermc.cumulus.component.InputComponent;
import org.geysermc.cumulus.component.SliderComponent;
import org.geysermc.cumulus.component.StepSliderComponent;
import org.geysermc.cumulus.component.ToggleComponent;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 使用 Floodgate 与 Cumulus 发送基岩版表单的 Provider。
 */
public final class CumulusProvider implements FormProvider {

    @Override
    public String name() {
        return "cumulus";
    }

    @Override
    public FormPlatform platform() {
        return FormPlatform.BEDROCK_FORM;
    }

    @Override
    public boolean supports(Player player) {
        return player != null && PlayerUtils.isBedrockPlayer(player);
    }

    @Override
    public boolean send(Player player, FormDefinition form, Consumer<FormResponse> responseHandler) {
        if (form instanceof FormDefinition.Simple simple) {
            return sendSimple(player, simple, responseHandler);
        }
        if (form instanceof FormDefinition.Modal modal) {
            return sendModal(player, modal, responseHandler);
        }
        if (form instanceof FormDefinition.Custom custom) {
            return sendCustom(player, custom, responseHandler);
        }
        return false;
    }

    /**
     * 发送简单表单。
     */
    private boolean sendSimple(
            Player player,
            FormDefinition.Simple form,
            Consumer<FormResponse> responseHandler
    ) {
        SimpleForm.Builder builder = SimpleForm.builder()
                .title(form.title())
                .content(form.content());
        for (ButtonDefinition button : form.buttons()) {
            if (button.imageType() == ButtonDefinition.ImageType.NONE) {
                builder.button(button.text());
                continue;
            }
            FormImage.Type imageType = button.imageType() == ButtonDefinition.ImageType.PATH
                    ? FormImage.Type.PATH
                    : FormImage.Type.URL;
            builder.button(button.text(), imageType, button.imageValue());
        }
        if (responseHandler != null) {
            builder.validResultHandler(response -> responseHandler.accept(
                            new FormResponse.Simple(response.clickedButtonId())
                    ))
                    .closedOrInvalidResultHandler(() -> responseHandler.accept(new FormResponse.Closed()));
        }
        return FloodgateApi.getInstance().sendForm(player.getUniqueId(), builder.build());
    }

    /**
     * 发送模态表单。
     */
    private boolean sendModal(
            Player player,
            FormDefinition.Modal form,
            Consumer<FormResponse> responseHandler
    ) {
        ModalForm.Builder builder = ModalForm.builder()
                .title(form.title())
                .content(form.content())
                .button1(form.button1())
                .button2(form.button2());
        if (responseHandler != null) {
            builder.validResultHandler(response -> responseHandler.accept(
                            new FormResponse.Modal(response.clickedButtonId() == 0)
                    ))
                    .closedOrInvalidResultHandler(() -> responseHandler.accept(new FormResponse.Closed()));
        }
        return FloodgateApi.getInstance().sendForm(player.getUniqueId(), builder.build());
    }

    /**
     * 发送自定义表单。
     */
    private boolean sendCustom(
            Player player,
            FormDefinition.Custom form,
            Consumer<FormResponse> responseHandler
    ) {
        CustomForm.Builder builder = CustomForm.builder().title(form.title());
        Map<String, Integer> componentIndexes = new LinkedHashMap<>();
        int componentIndex = 0;
        for (CustomComponent component : form.components()) {
            addComponent(builder, component);
            componentIndexes.put(component.id(), componentIndex++);
        }
        if (responseHandler != null) {
            builder.validResultHandler(response -> {
                        Map<String, Object> values = new LinkedHashMap<>();
                        componentIndexes.forEach((id, index) -> {
                            try {
                                values.put(id, response.valueAt(index));
                            } catch (IllegalArgumentException exception) {
                                values.put(id, null);
                            }
                        });
                        responseHandler.accept(new FormResponse.Custom(values));
                    })
                    .closedOrInvalidResultHandler(() -> responseHandler.accept(new FormResponse.Closed()));
        }
        return FloodgateApi.getInstance().sendForm(player.getUniqueId(), builder.build());
    }

    /**
     * 将平台无关组件添加到 Cumulus 自定义表单。
     */
    private void addComponent(CustomForm.Builder builder, CustomComponent component) {
        if (component instanceof CustomComponent.Label label) {
            builder.label(label.text());
            return;
        }
        if (component instanceof CustomComponent.Input input) {
            builder.component(InputComponent.of(
                    input.text(),
                    input.placeholder(),
                    input.defaultValue()
            ));
            return;
        }
        if (component instanceof CustomComponent.Toggle toggle) {
            builder.component(ToggleComponent.of(toggle.text(), toggle.defaultValue()));
            return;
        }
        if (component instanceof CustomComponent.Slider slider) {
            builder.component(SliderComponent.of(
                    slider.text(),
                    slider.min(),
                    slider.max(),
                    slider.step(),
                    slider.defaultValue()
            ));
            return;
        }
        if (component instanceof CustomComponent.StepSlider stepSlider) {
            builder.component(StepSliderComponent.of(
                    stepSlider.text(),
                    stepSlider.steps(),
                    stepSlider.defaultStep()
            ));
            return;
        }
        if (component instanceof CustomComponent.Dropdown dropdown) {
            builder.component(DropdownComponent.of(
                    dropdown.text(),
                    dropdown.options(),
                    dropdown.defaultOption()
            ));
        }
    }
}
