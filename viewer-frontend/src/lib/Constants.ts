export default interface Message {
    id: number;
    authorId: number;
    alias: string;
    postDate: number;
    subject: string | null;
    body: string;
    nextInTime: number;
    error?: string;
}

export default interface Page {
    messages: Message[];
    previousPageIndex: number | null;
    nextPageIndex: number | null;
    error?: string;
}

export default interface ResultData {
    results: Message[];
    isFinalPage: boolean;
    error?: string;
}

export default interface User {
    id: number;
    userName: string | null;
    knownAliases: string[];
    knownGroups: string[];
    fakeAccount: boolean;
    error?: string;
}

export default interface GroupData {
    groups: string[];
}
