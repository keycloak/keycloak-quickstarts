import { createInstance } from "i18next";
import HttpBackend from "i18next-http-backend";
import { initReactI18next } from "react-i18next";
import { environment } from "./environment";

type KeyValue = { key: string; value: string };

export const i18n = createInstance({
  fallbackLng: "en",
  keySeparator: ".",
  interpolation: {
    escapeValue: false,
  },
  defaultNS: [environment.realm],
  ns: [environment.realm],
  backend: {
    loadPath: `${environment.serverBaseUrl}/resources/{{ns}}/admin/{{lng}}`,
    parse: (data: string) => {
      const messages = JSON.parse(data);

      const result: Record<string, string> = {};
      messages.forEach((v: KeyValue) => (result[v.key] = v.value));
      return result;
    },
  },
});

i18n.use(HttpBackend);
i18n.use(initReactI18next);
