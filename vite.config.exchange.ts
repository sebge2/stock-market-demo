import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { nxViteTsPaths } from '@nx/vite/plugins/nx-tsconfig-paths.plugin';

export default defineConfig({
  root: './apps/frontend-exchange',
  plugins: [nxViteTsPaths(), react()],
  server: {
    port: 3001,
    host: 'localhost',
    strictPort: false,
    fs: {
      allow: ['..', '../..'],
    },
  },
  build: {
    outDir: '../../dist/apps/frontend-exchange',
    reportCompressedSize: true,
    commonjsOptions: {
      transformMixedEsModules: true,
    },
  },
});
