import {
  AccountEnvironment,
  Page,
  UserRepresentation,
  getPersonalInfo,
  savePersonalInfo,
  useAlerts,
  useEnvironment,
  usePromise,
} from "@keycloak/keycloak-account-ui";
import { ActionGroup, Button, Form, FormGroup, TextInput } from "@patternfly/react-core";
import { useState } from "react";
import { useTranslation } from "react-i18next";

export const MyPage = () => {
  const { t } = useTranslation();
  const context = useEnvironment<AccountEnvironment>();
  const [personalInfo, setPersonalInfo] = useState<UserRepresentation>();
  const { addAlert, addError } = useAlerts();

  usePromise((signal) => getPersonalInfo({ signal, context }), setPersonalInfo);

  const onSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    try {
      await savePersonalInfo(context, personalInfo);
      addAlert(t("myPage.save.success"));
    } catch (error) {
      console.error(error);
      addError("Not able to save personal info");
    }
  };

  return (
    <Page
      title={"My personal info page"}
      description={"Example of a personal info page"}
    >
      <Form isHorizontal onSubmit={onSubmit}>
        <FormGroup label={t("email")} fieldId="email">
          <TextInput
            name="email"
            value={personalInfo?.email}
            onChange={(_, value) =>
              setPersonalInfo({ ...personalInfo, email: value })
            }
          />
        </FormGroup>
        <ActionGroup>
          <Button
            data-testid="save"
            type="submit"
            id="save-btn"
            variant="primary"
          >
            {t("save")}
          </Button>
        </ActionGroup>
      </Form>
    </Page>
  );
};
