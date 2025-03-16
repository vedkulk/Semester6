import { NextApiRequest, NextApiResponse } from "next";
import { DirectoryLoader } from "langchain/document_loaders/fs/directory";
import { PDFLoader } from "@langchain/community/document_loaders/fs/pdf";
import { TextLoader } from "langchain/document_loaders/fs/text";

export default async function handler(req: NextApiRequest, res: NextApiResponse) {
    if (req.method === "POST") {
        try {
            const { indexname, namespace } = JSON.parse(req.body);
            await handleUpload(indexname, namespace, res);
        } catch (error) {
            res.status(400).json({ error: "Invalid request body" });
        }
    } else {
        res.status(405).json({ error: "Method Not Allowed" });
    }
}
async function handleUpload(indexname: string, namespace: string, res: NextApiResponse) {
    const loader = new DirectoryLoader('./documents', {
        '.pdf': (path: string) => new PDFLoader(path,
            {
                splitPages: false
            }
        ),
        '.txt': (path: string) => new TextLoader(path)
    })
    const docs = await loader.load();
}

