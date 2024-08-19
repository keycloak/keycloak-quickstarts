import { LanguageDetectorModule, createInstance } from "i18next";
import HttpBackend from "i18next-http-backend";
import { initReactI18next } from "react-i18next";
import { environment } from "./environment";

type KeyValue = { key: string; value: string };

export const keycloakLanguageDetector: LanguageDetectorModule = {
  type: "languageDetector",

  detect() {
    return environment.locale;
  },
};

export const i18n = createInstance({
  fallbackLng: "en",
  interpolation: {
    escapeValue: false,
  },
  backend: {
    loadPath: `${environment.serverBaseUrl}/resources/${environment.realm}/account/{{lng}}`,
    parse: (data: string) => {
      const messages = JSON.parse(data);

      const result: Record<string, string> = {};
      messages.forEach((v: KeyValue) => (result[v.key] = v.value));
      return result;
    },
  },
});

i18n.use(HttpBackend);
i18n.use(keycloakLanguageDetector);
i18n.use(initReactI18next);
