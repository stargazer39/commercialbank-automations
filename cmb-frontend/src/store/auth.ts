import { create } from "zustand";
import { createJSONStorage, persist } from "zustand/middleware";

interface AuthStore {
  accessToken: string;
  setAccessToken: (token: string) => void;
}

const useAuthStore = create<AuthStore>()(
  persist<AuthStore>(
    (set, _get) => ({
      accessToken: "",
      setAccessToken: (token: string) => set({ accessToken: token }),
    }),
    {
      name: "auth-store", // name of the item in the storage (must be unique)
      storage: createJSONStorage(() => sessionStorage), // (optional) by default, 'localStorage' is used
    }
  )
);

export { useAuthStore };
export type { AuthStore };
