import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { nxViteTsPaths } from '@nx/vite/plugins/nx-tsconfig-paths.plugin';

export default defineConfig({
  plugins: [nxViteTsPaths(), react()],
  server: {
    port: 3000,
    host: 'localhost',
    strictPort: false,
    fs: {
      allow: ['..', '../..'],
    },
  },
  build: {
    outDir: '../../dist/bank/front',
    reportCompressedSize: true,
    commonjsOptions: {
      transformMixedEsModules: true,
    },
  },
});
