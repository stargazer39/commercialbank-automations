import * as React from "react";
import { createLazyFileRoute, useNavigate } from "@tanstack/react-router";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { login } from "../api/auth";
import { useAuthStore } from "../store/auth";

export const Route = createLazyFileRoute("/login")({
  component: RouteComponent,
});

function RouteComponent() {
  const navigate = useNavigate();

  const qc = useQueryClient();

  const authStore = useAuthStore();
  const mutation = useMutation({
    mutationFn: login,
    onSuccess: (response) => {
      authStore.setAccessToken(response.accessToken);
      qc.invalidateQueries({ queryKey: ["transactions"] });
      navigate({ to: "/transactions" });
    },
  });

  const onSubmit: React.FormEventHandler<HTMLFormElement> = (e) => {
    e.preventDefault();
    const credentials = new FormData(e.currentTarget);
    mutation.mutate(Object.fromEntries(credentials) as any);
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
        <h2 className="text-2xl font-bold text-center text-gray-700">Login</h2>
        <form onSubmit={onSubmit} className="mt-4 space-y-4">
          <div>
            <label
              className="block mb-2 text-sm font-medium text-gray-600"
              htmlFor="email"
            >
              Email Address
            </label>
            <input
              type="text"
              id="username"
              name="username"
              className="w-full px-4 py-2 text-sm text-gray-700 bg-gray-100 border rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:bg-white"
              required
            />
          </div>
          <div>
            <label
              className="block mb-2 text-sm font-medium text-gray-600"
              htmlFor="password"
            >
              Password
            </label>
            <input
              type="password"
              id="password"
              name="password"
              className="w-full px-4 py-2 text-sm text-gray-700 bg-gray-100 border rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:bg-white"
              required
            />
          </div>
          <div className="flex items-center justify-between">
            <div>
              <input
                type="checkbox"
                id="remember"
                className="text-indigo-600"
              />
              <label htmlFor="remember" className="ml-2 text-sm text-gray-600">
                Remember me
              </label>
            </div>
            <a href="#" className="text-sm text-indigo-500 hover:underline">
              Forgot password?
            </a>
          </div>
          <button
            type="submit"
            className="w-full px-4 py-2 text-sm font-medium text-white bg-indigo-500 rounded-md hover:bg-indigo-600 focus:outline-none focus:ring-2 focus:ring-indigo-400"
          >
            Login
          </button>
        </form>
        <p className="text-sm text-center text-gray-600">
          Don't have an account?{" "}
          {/* <a href="#" className="text-indigo-500 hover:underline">
            Sign up
          </a> */}
          Tough shit!
        </p>
      </div>
    </div>
  );
}
