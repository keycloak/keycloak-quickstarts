import { createInstance } from "i18next";
import FetchBackend from "i18next-fetch-backend";
import { initReactI18next } from "react-i18next";
import { environment } from "./environment";

type KeyValue = { key: string; value: string };

export const i18n = createInstance({
  fallbackLng: "en",
  interpolation: {
    escapeValue: false,
  },
  backend: {
    loadPath: `${environment.serverBaseUrl}/resources/${environment.realm}/admin/{{lng}}`,
    parse: (data: string) => {
      const messages = JSON.parse(data);

      const result: Record<string, string> = {};
      messages.forEach((v: KeyValue) => (result[v.key] = v.value));
      return result;
    },
  },
});

i18n.use(FetchBackend);
i18n.use(initReactI18next);